package com.lhxy.istationdevice.android11.domain.module.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class SignInStateTest {
    @Test
    public void applyCard_preservesLongM90RfidValue() {
        SignInState state = new SignInState();

        state.applyCard("38383838323233431000000000000000");

        assertEquals("38383838323233431000000000000000", state.getCardNo());
        assertTrue(state.isSignedIn());
        assertFalse(state.hasResolvedDriverIdentity());
        assertEquals("司机 0000", state.getDriverName());
    }

    @Test
    public void applyCard_keepsShortCardValue() {
        SignInState state = new SignInState();

        state.applyCard("11223344");

        assertEquals("11223344", state.getCardNo());
        assertTrue(state.isSignedIn());
    }

    @Test
    public void applyCard_allZeroValueFallsBackToEmptyCard() {
        SignInState state = new SignInState();

        state.applyCard("0000000000000000");

        assertEquals("00000000", state.getCardNo());
    }
}