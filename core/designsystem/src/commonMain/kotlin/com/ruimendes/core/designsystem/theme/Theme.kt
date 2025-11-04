package com.ruimendes.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    // Button states
    val primaryHover: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,

    // Text variants
    val textPrimary: Color,
    val textTertiary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,

    // Surface variants
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val overlay: Color,

    // Accent colors
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentLightBlue: Color,
    val accentGrey: Color,

    // Cake colors for chat bubbles
    val cakeViolet: Color,
    val cakeGreen: Color,
    val cakeBlue: Color,
    val cakePink: Color,
    val cakeOrange: Color,
    val cakeYellow: Color,
    val cakeTeal: Color,
    val cakePurple: Color,
    val cakeRed: Color,
    val cakeMint: Color,
)

val LightExtendedColors = ExtendedColors(
    primaryHover = Brand600,
    destructiveHover = Red600,
    destructiveSecondaryOutline = Red200,
    disabledOutline = Base200,
    disabledFill = Base150,
    successOutline = Brand100,
    success = Brand600,
    onSuccess = Base0,
    secondaryFill = Base100,

    textPrimary = Base1000,
    textTertiary = Base800,
    textSecondary = Base900,
    textPlaceholder = Base700,
    textDisabled = Base400,

    surfaceLower = Base100,
    surfaceHigher = Base100,
    surfaceOutline = Base1000Alpha14,
    overlay = Base1000Alpha80,

    accentBlue = Blue,
    accentPurple = Purple,
    accentViolet = Violet,
    accentPink = Pink,
    accentOrange = Orange,
    accentYellow = Yellow,
    accentGreen = Green,
    accentTeal = Teal,
    accentLightBlue = LightBlue,
    accentGrey = Grey,

    cakeViolet = CakeLightViolet,
    cakeGreen = CakeLightGreen,
    cakeBlue = CakeLightBlue,
    cakePink = CakeLightPink,
    cakeOrange = CakeLightOrange,
    cakeYellow = CakeLightYellow,
    cakeTeal = CakeLightTeal,
    cakePurple = CakeLightPurple,
    cakeRed = CakeLightRed,
    cakeMint = CakeLightMint,
)

val DarkExtendedColors = ExtendedColors(
    primaryHover = Brand600,
    destructiveHover = Red600,
    destructiveSecondaryOutline = Red200,
    disabledOutline = Base900,
    disabledFill = Base1000,
    successOutline = Brand500Alpha40,
    success = Brand500,
    onSuccess = Base1000,
    secondaryFill = Base900,

    textPrimary = Base0,
    textTertiary = Base200,
    textSecondary = Base150,
    textPlaceholder = Base400,
    textDisabled = Base500,

    surfaceLower = Base1000,
    surfaceHigher = Base900,
    surfaceOutline = Base100Alpha10Alt,
    overlay = Base1000Alpha80,

    accentBlue = Blue,
    accentPurple = Purple,
    accentViolet = Violet,
    accentPink = Pink,
    accentOrange = Orange,
    accentYellow = Yellow,
    accentGreen = Green,
    accentTeal = Teal,
    accentLightBlue = LightBlue,
    accentGrey = Grey,

    cakeViolet = CakeDarkViolet,
    cakeGreen = CakeDarkGreen,
    cakeBlue = CakeDarkBlue,
    cakePink = CakeDarkPink,
    cakeOrange = CakeDarkOrange,
    cakeYellow = CakeDarkYellow,
    cakeTeal = CakeDarkTeal,
    cakePurple = CakeDarkPurple,
    cakeRed = CakeDarkRed,
    cakeMint = CakeDarkMint,
)

val LightColorScheme = lightColorScheme(
    primary = Brand500,
    onPrimary = Brand1000,
    primaryContainer = Brand100,
    onPrimaryContainer = Brand900,

    secondary = Base700,
    onSecondary = Base0,
    secondaryContainer = Base100,
    onSecondaryContainer = Base900,

    tertiary = Brand900,
    onTertiary = Base0,
    tertiaryContainer = Brand100,
    onTertiaryContainer = Brand1000,

    error = Red500,
    onError = Base0,
    errorContainer = Red200,
    onErrorContainer = Red600,

    background = Brand1000,
    onBackground = Base0,
    surface = Base0,
    onSurface = Base1000,
    surfaceVariant = Base100,
    onSurfaceVariant = Base900,

    outline = Base1000Alpha8,
    outlineVariant = Base200,
)

val DarkColorScheme = darkColorScheme(
    primary = Brand500,
    onPrimary = Brand1000,
    primaryContainer = Brand900,
    onPrimaryContainer = Brand500,

    secondary = Base400,
    onSecondary = Base1000,
    secondaryContainer = Base900,
    onSecondaryContainer = Base150,

    tertiary = Brand500,
    onTertiary = Base1000,
    tertiaryContainer = Brand900,
    onTertiaryContainer = Brand500,

    error = Red500,
    onError = Base0,
    errorContainer = Red600,
    onErrorContainer = Red200,

    background = Base1000,
    onBackground = Base0,
    surface = Base950,
    onSurface = Base0,
    surfaceVariant = Base900,
    onSurfaceVariant = Base150,

    outline = Base100Alpha10,
    outlineVariant = Base800,
)