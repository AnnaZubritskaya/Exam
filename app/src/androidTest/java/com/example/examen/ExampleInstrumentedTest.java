package com.example.examen;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private DatabaseConnector connector;
    @Before
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.examen", appContext.getPackageName());
        connector=new DatabaseConnector(appContext);
    }
    @After public void finish() {connector.close();}
    @Test public void testAPreConditions() {assertNotNull(connector);}
    @Test public void testBookAdd() {
        //int rowCount = connector.getRowCount("Tabel_Book");
        //connector.insertBook("name", "genre", "author");
        //int newRowCount=connector.getRowCount("Tabel_Book");;
        //assertEquals(newRowCount, rowCount+1);
    }
    @Test public void testReaderAdd() {
        //int rowCount = connector.getRowCount("Table_Reader");
        //connector.insertReader("name");
        //int newRowCount=connector.getRowCount("Table_Reader");
        //assertEquals(newRowCount, rowCount+1);
    }
    @Test public void testCardAdd() {
        //int rowCount = connector.getRowCount("Table_Card");
        //connector.insertCard(1, 1, "11/06/2023");
        //int newRowCount=connector.getRowCount("Table_Card");
        //assertEquals(newRowCount, rowCount+1);
    }
    @Test public void testBookDel() {
        //int rowCount = connector.getRowCount("Tabel_Book");
        //connector.deleteBook(24);
        //int newRowCount=connector.getRowCount("Tabel_Book");
        //assertEquals(newRowCount, rowCount-1);
    }
    @Test public void testReaderDel() {
        //int rowCount = connector.getRowCount("Table_Reader");
        //connector.deleteReader(17);
        //int newRowCount=connector.getRowCount("Table_Reader");
        //assertEquals(newRowCount, rowCount-1);
    }
    @Test public void testCardDel() {
        //int rowCount = connector.getRowCount("Table_Card");
        //connector.deleteCard(17);
        //int newRowCount=connector.getRowCount("Table_Card");
        //assertEquals(newRowCount, rowCount-1);
    }
    @Test public void testBookUpdate() {
        connector.updateBook(25, "new name", "new genre", "new author");
    }
    @Test public void testReaderUpdate() {
        connector.updateReader(19, "new name");
    }
    @Test public void testCardUpdate() {
        connector.updateCard(19, 10, 10, "01/01/2021");
    }

}