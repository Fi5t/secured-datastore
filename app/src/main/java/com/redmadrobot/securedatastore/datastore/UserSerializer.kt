package com.redmadrobot.securedatastore.datastore

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.google.crypto.tink.Aead
import com.google.protobuf.InvalidProtocolBufferException
import com.redmadrobot.securedatastore.User
import java.io.InputStream
import java.io.OutputStream


class UserSerializer(private val aead: Aead) : Serializer<User> {
    override fun readFrom(input: InputStream): User {
        return try {
            val encryptedInput = input.readBytes()

            val decryptedInput = if (encryptedInput.isNotEmpty()) {
                aead.decrypt(encryptedInput, null)
            } else {
                encryptedInput
            }

            User.parseFrom(decryptedInput)

        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Error deserializing proto", e)
        }
    }

    override fun writeTo(user: User, output: OutputStream) {
        val encryptedBytes = aead.encrypt(user.toByteArray(), null)

        output.write(encryptedBytes)
    }
}
