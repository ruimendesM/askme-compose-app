package com.ruimendes.core.data.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JwtDecoderTest {

    @Test
    fun `decodeJwtRole returns role from valid token`() {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ1c2VyMSIsInJvbGUiOiJBRE1JTiJ9." +
            "signature"
        assertEquals("ADMIN", decodeJwtRole(token))
    }

    @Test
    fun `decodeJwtRole returns USER role`() {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ1c2VyMiIsInJvbGUiOiJVU0VSIn0." +
            "signature"
        assertEquals("USER", decodeJwtRole(token))
    }

    @Test
    fun `decodeJwtRole returns null when no role claim`() {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiJ1c2VyMyJ9." +
            "signature"
        assertNull(decodeJwtRole(token))
    }

    @Test
    fun `decodeJwtRole returns null for malformed token`() {
        assertNull(decodeJwtRole("not-a-jwt"))
    }

    @Test
    fun `decodeJwtRole returns null for empty string`() {
        assertNull(decodeJwtRole(""))
    }
}
