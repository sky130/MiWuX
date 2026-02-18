package miwu.compose.wrapper.base

sealed interface Zone {
    object Header : Zone
    object SubHeader : Zone
    object Body : Zone
    object SubFooter : Zone
    object Footer : Zone
    object Unspecified : Zone
}