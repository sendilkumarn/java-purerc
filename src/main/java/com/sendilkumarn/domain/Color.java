package com.sendilkumarn.domain;

public enum Color {
    BLACK, /** In use or free. */
    GRAY, /** Possible member of cycle. */
    WHITE,/** Member of cycle. */
    PURPLE,/** Possible root of cycle. */
    GREEN, /** Acyclic. */
    RED,/** Candidate cycle undergoing sigma-computation. */
    ORANGE/** Candidate cycle awaiting epoch boundary. */
}
