package com.redmadrobot.securedatastore.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.crypto.tink.Aead
import com.redmadrobot.securedatastore.User
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream


class UserSerializer(private val aead: Aead) : Serializer<User> {
    override suspend fun readFrom(input: InputStream): User {
        return try {
            val encryptedInput = input.readBytes()

            val decryptedInput = if (encryptedInput.isNotEmpty()) {
                aead.decrypt(encryptedInput, null)
            } else {
                encryptedInput
            }

            ProtoBuf.decodeFromByteArray(User.serializer(), decryptedInput)
        } catch (e: SerializationException) {
            throw CorruptionException("Error deserializing proto", e)
        }
    }

    override suspend fun writeTo(user: User, output: OutputStream) {
        val byteArray = ProtoBuf.encodeToByteArray(User.serializer(), user)
        val encryptedBytes = aead.encrypt(byteArray, null)

        output.write(encryptedBytes)
    }

    override val defaultValue: User =
        User()
}
