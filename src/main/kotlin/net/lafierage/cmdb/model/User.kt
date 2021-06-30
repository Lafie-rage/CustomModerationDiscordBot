package net.lafierage.cmdb.model

import com.google.api.services.sheets.v4.model.ValueRange


interface User {
    fun toValueList(): ValueRange
}