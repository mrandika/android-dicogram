package space.mrandika.dicogram.utils.dummy

import space.mrandika.dicogram.data.model.remote.GenericAPIResponse
import space.mrandika.dicogram.data.model.remote.LoginResponse
import space.mrandika.dicogram.data.model.remote.User

object AuthDummy {
    fun generateDummyLoginResponse(): LoginResponse {
        val user = User(
            userId = "user-kRfMxsuSfqCuzvHE",
            name = "test",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWtSZk14c3VTZnFDdXp2SEUiLCJpYXQiOjE2ODI4NTU5Mzd9.9RvAZ8I_EEQjq_w9-GThyDJAdZEj_MO4st4azuqE2cs"
        )

        return LoginResponse(
            user = user,
            error = false,
            message = "success"
        )
    }

    fun generateDummyRegisterResponse(): GenericAPIResponse {
        return GenericAPIResponse(
            error = false,
            message = "success"
        )
    }
}