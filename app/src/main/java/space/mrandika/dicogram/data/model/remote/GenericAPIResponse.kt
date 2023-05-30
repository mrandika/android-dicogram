package space.mrandika.dicogram.data.model.remote

import com.google.gson.annotations.SerializedName

data class GenericAPIResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
