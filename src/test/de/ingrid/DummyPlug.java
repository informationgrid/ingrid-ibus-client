package de.ingrid;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;

public class DummyPlug implements IPlug {

    public DummyPlug() {
        // TODO Auto-generated constructor stub
    }

    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    public void configure(PlugDescription arg0) throws Exception {
        // TODO Auto-generated method stub

    }

    public IngridHits search(IngridQuery arg0, int arg1, int arg2) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public IngridHitDetail getDetail(IngridHit arg0, IngridQuery arg1, String[] arg2) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public IngridHitDetail[] getDetails(IngridHit[] arg0, IngridQuery arg1, String[] arg2) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
