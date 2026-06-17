package org.battleplugins.arena.module.diamondbank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiamondAmountsTest {

    @Test
    void wholeDiamondsConvertAtNineShardsEach() {
        assertEquals(0L, DiamondAmounts.parseToShards("0"));
        assertEquals(9L, DiamondAmounts.parseToShards("1"));
        assertEquals(9L, DiamondAmounts.parseToShards("1.0"));
        assertEquals(18L, DiamondAmounts.parseToShards("2"));
        assertEquals(900L, DiamondAmounts.parseToShards("100"));
    }

    @Test
    void singleDecimalDigitIsShardsNotAFraction() {
        // 1 diamond + 5 shards = 14 shards (base-9 notation, not 1.5 * 9).
        assertEquals(14L, DiamondAmounts.parseToShards("1.5"));
        assertEquals(5L, DiamondAmounts.parseToShards("0.5"));
        assertEquals(8L, DiamondAmounts.parseToShards("0.8"));
        assertEquals(21L, DiamondAmounts.parseToShards("2.3"));
    }

    @Test
    void negativeAmountsAreSignedWithdrawals() {
        assertEquals(-9L, DiamondAmounts.parseToShards("-1"));
        assertEquals(-14L, DiamondAmounts.parseToShards("-1.5"));
        assertEquals(9L, DiamondAmounts.parseToShards("+1"));
    }

    @Test
    void whitespaceIsTolerated() {
        assertEquals(14L, DiamondAmounts.parseToShards("  1.5  "));
    }

    @Test
    void zeroVariantsAreCleanNoOps() {
        assertEquals(0L, DiamondAmounts.parseToShards("0.0"));
        assertEquals(0L, DiamondAmounts.parseToShards("-0"));
        assertEquals(0L, DiamondAmounts.parseToShards("0.0"));
    }

    @Test
    void leadingAndTrailingDotsAreAccepted() {
        assertEquals(5L, DiamondAmounts.parseToShards(".5"));
        assertEquals(9L, DiamondAmounts.parseToShards("1."));
    }

    @Test
    void signOnlyOrDotOnlyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("-"));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("+"));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("."));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("-."));
    }

    @Test
    void nonAsciiDigitsAreRejected() {
        // Arabic-Indic digit five (U+0665) is a digit to Character.isDigit but not ASCII.
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("٥"));
    }

    @Test
    void matchesDiamondBankConversionForLargeWholeValues() {
        // 10 million diamonds would render as "1.0E7" via Float and break the
        // upstream string-split converter; the string parser handles it.
        assertEquals(90_000_000L, DiamondAmounts.parseToShards("10000000"));
    }

    @Test
    void moreThanOneDecimalDigitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("1.25"));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("1.05"));
    }

    @Test
    void nonNumericInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("abc"));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("1.x"));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards(""));
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards(null));
    }

    @Test
    void overflowIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> DiamondAmounts.parseToShards("99999999999999999999"));
    }
}
