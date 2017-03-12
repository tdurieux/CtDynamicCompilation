import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExampleTest {

	@Test
	public void test1(){
		Example ex = new Example();
		assertEquals('a', ex.charAt("abcd", 0));
	}
	
	@Test
	public void test2(){
		Example ex = new Example();
		assertEquals('d', ex.charAt("abcd", 3));
	}
	
	@Test
	public void test3(){
		Example ex = new Example();
		String s = "abcd";
		assertEquals('d', ex.charAt(s, s.length()-1));
	}
	
	@Test
	public void test4(){
		Example ex = new Example();
		String s = "abcd";
		assertEquals('d', ex.charAt(s, 12));
	}
	
	@Test
	public void test5(){
		Example ex = new Example();
		String s = "abcd";
		assertEquals('a', ex.charAt(s, -5));
	}
	
	@Test
	public void test6(){
		Example ex = new Example();
		String s = "abcd";
		assertEquals('a', ex.charAt(s, -1));
	}
	
	@Test
	public void test7(){
		Example ex = new Example();
		assertEquals('c', ex.charAt("abcd", 2));
	}
	
	@Test
	public void test8(){
		Example ex = new Example();
		assertEquals('b', ex.charAt("abcd", 1));
	}
	
	@Test
	public void test9(){
		Example ex = new Example();
		assertEquals('f', ex.charAt("abcdefghijklm", 5));
	}
}
