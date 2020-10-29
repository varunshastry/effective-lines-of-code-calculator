/*
 * Copyright (C) 2008 varun
 *
 * This file is part of LOC Calculator.
 *
 * LOC Calculator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LOC Calculator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LOC Calculator.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.varun.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.varun.benchmark.Timer;

/**
 * @author varun
 */
public class TimerTest
{

	/**
	 * Test method for {@link org.varun.benchmark.Timer#Timer()}.
	 */
	@Test
	public void testConstructor()
	{
		Timer timer = new Timer();
		assertEquals( timer.getTimeInMillis(), 0D );
		assertEquals( timer.getTimeInSec(), 0D );
	}

	/**
	 * Test method for {@link org.varun.benchmark.Timer#getTimeInMillis()}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetTimeInMillis() throws InterruptedException
	{

		Timer timer = new Timer();
		timer.start();
		Thread.sleep( 1000 );
		timer.end();

		assertTrue( timer.getTimeInMillis() >= 1000D );
	}

	/**
	 * Test method for {@link org.varun.benchmark.Timer#getTimeInSec()}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetTimeInSec() throws InterruptedException
	{

		Timer timer = new Timer();
		timer.start();
		Thread.sleep( 1000 );
		timer.end();

		assertTrue( timer.getTimeInSec() >= 1D );

	}

	/**
	 * Test method for {@link org.varun.benchmark.Timer#reset()}.
	 * @throws InterruptedException 
	 */
	@Test
	public void testReset() throws InterruptedException
	{

		Timer timer = new Timer();
		timer.start();
		Thread.sleep( 1000 );
		timer.end();
		timer.reset();

		assertEquals( timer.getTimeInMillis(), 0D );
		assertEquals( timer.getTimeInSec(), 0D );
	}
}
