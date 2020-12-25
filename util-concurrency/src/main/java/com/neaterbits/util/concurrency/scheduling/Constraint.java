package com.neaterbits.util.concurrency.scheduling;

public enum Constraint {
	IO,
	NETWORK,
	CPU,
	CPU_IO_CACHED; // most often disk cached
}
