/*
 * **************************************************-
 * Ingrid iBus Client
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.bus.client;

import java.util.Random;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;

/**
 * 
 */
public class DummyIPlug implements IPlug {

    /***/
    public static final String SUMMARY = "a summary";

    /***/
    public static final String TITLE = "a title";

    private String fMyPlugId;

    private PlugDescription fPlugDescription;
    
    private Random _random = new Random(System.currentTimeMillis());

    /**
     * 
     */
    public DummyIPlug() {
	// for serialisations
    }

    /**
     * @param plugId
     */
    public DummyIPlug(String plugId) {
	this.fMyPlugId = plugId;
    }

    public IngridHits search(IngridQuery query, int start, int lenght) {
	IngridHit[] hit = null;
	if (query.getGrouped() != null && query.getGrouped().equals(IngridQuery.GROUPED_BY_DATASOURCE)) {
	    hit = new IngridHit[1];
	    hit[0] = new IngridHit(this.fMyPlugId, 23, 23, 23f);
	    hit[0].setGroupTotalHitLength(2);
	} else {
	    hit = new IngridHit[2];
	    hit[0] = new IngridHit(this.fMyPlugId, 23, 23, _random.nextInt());
	    hit[0].setGroupTotalHitLength(2);
	    hit[1] = new IngridHit(this.fMyPlugId, 23, 23, _random.nextInt());
	    hit[1].setGroupTotalHitLength(2);
	}
	return new IngridHits(this.fMyPlugId, hit.length, hit, true);
    }

    public void configure(PlugDescription arg0) throws Exception {
	this.fPlugDescription = arg0;
    }

    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] fields) throws Exception {
	IngridHitDetail detail = new IngridHitDetail(hit, TITLE, SUMMARY);
	for (int i = 0; i < fields.length; i++) {
	    if (fields[i].equals(PlugDescription.PARTNER)) {
		detail.setArray(PlugDescription.PARTNER, this.fPlugDescription.getPartners());
	    } else if (fields[i].equals(PlugDescription.PROVIDER)) {
		detail.setArray(PlugDescription.PROVIDER, this.fPlugDescription.getProviders());
	    }
	}
	return detail;

    }

    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
	return new IngridHitDetail[] { new IngridHitDetail(hits[0], TITLE, SUMMARY) };
    }

    public void close() throws Exception {
	// TODO Auto-generated method stub

    }

    @Override
    public IngridDocument call(IngridCall arg0) throws Exception {
        return null;
    }
}
