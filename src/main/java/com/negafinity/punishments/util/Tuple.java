package com.negafinity.punishments.util;

import com.google.common.base.Objects;

public class Tuple<K, V>
{
	public static <K, V> Tuple<K, V> of(K first, V second)
	{
		return new Tuple<>(first, second);
	}

	private final K first;
	private final V second;

	public Tuple(K first, V second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets the first object, otherwise known as "key".
	 *
	 * @return The first object
	 */
	public K getFirst()
	{
		return this.first;
	}

	/**
	 * Gets the second object, otherwise known as "value".
	 *
	 * @return The value
	 */
	public V getSecond()
	{
		return this.second;
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
			.add("first", this.first)
			.add("second", this.second)
			.toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.first, this.second);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Tuple other = (Tuple) obj;
		return Objects.equal(this.first, other.first) && Objects.equal(this.second, other.second);
	}
}
