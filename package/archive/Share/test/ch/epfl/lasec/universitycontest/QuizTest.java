package ch.epfl.lasec.universitycontest;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuizTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQuizEncoded() {
		Quiz q = null;
		try {
			q = new Quiz("2012-06-15","Quiz 1");
		} catch (ParseException e) {
		}
		q.addQuestion(new QuizQuestion("What is your quest?"));
		q.addMultipleChoiceQuestion(new MultipleChoiceQuestion(
				"what is your favourit colour?", new String[] { "blue", "read",
						"green", "yellow" }));
		q.addQuestion(new QuizQuestion("what is your name?"));
		q.addQuestion(new QuizQuestion("What is the capital of Assyria?"));
		q.addMultipleChoiceQuestion(new MultipleChoiceQuestion(
				"What is the air-speed velocity of an unladen swallow?",
				new String[] {
						"What do you mean?  An African or European swallow?",
						"I don't know" }));
		assertEquals("Error: quiz != dec(enc(quiz))", q,
				Quiz.fromEncoded(q.getEncoded()));
		q.setQuestionAnswer(0, "To seek the holy grail");
		assertEquals(q, Quiz.fromEncoded(q.getEncoded()));
	}

	@Test
	public void testQuizQuestionEncoded(){
		QuizQuestion q1 = new QuizQuestion("what is your name?");
		MultipleChoiceQuestion q2 = new MultipleChoiceQuestion("what is your favourit colour?",
				new String [] {"blue","read","green","yellow"});
		assertEquals("Error: q1 != dec(enc(q1))",
				q1, QuizQuestion.fromEncoded(q1.getEncoded()));
		assertEquals("Error: q2 != dec(enc(q2))", 
				q2, MultipleChoiceQuestion.fromEncoded(q2.getEncoded()));
	}
	
}
