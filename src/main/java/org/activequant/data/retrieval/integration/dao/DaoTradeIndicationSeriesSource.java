/****

    activequant - activestocks.eu

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

	
	contact  : contact@activestocks.eu
    homepage : http://www.activestocks.eu

****/
package org.activequant.data.retrieval.integration.dao;

import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.core.domainmodel.TradeIndicationSeries;
import org.activequant.dao.ITradeIndicationDao;
import org.activequant.data.retrieval.integration.TradeIndicationSeriesSourceBase;
import org.apache.log4j.Logger;

/**
 * Trade indication series source that fetches data from the database.
 * <br>
 * <b>History:</b><br>
 *  - [11.12.2007] Created (Ulrich Staudinger)<br>
 *  - [11.12.2007] Implemented (Mike Kroutikov)<br>
 *
 *  @author Ulrich Staudinger
 *  @author Mike Kroutikov
 */
public class DaoTradeIndicationSeriesSource extends TradeIndicationSeriesSourceBase {
	
	private final Logger log = Logger.getLogger(getClass());
	
	private ITradeIndicationDao dao;
	
	public ITradeIndicationDao getDao() {
		return dao;
	}
	public void setDao(ITradeIndicationDao val) {
		dao = val;
	}

	public TradeIndicationSeries fetch(SeriesSpecification specs)
			throws Exception {
		if(dao == null) {
			throw new IllegalStateException("dao property not set");
		}
		
		TradeIndicationSeries series = new TradeIndicationSeries(specs);

		log.debug("populating series from the data source: " + specs);
		series.setTradeIndications(dao.findBySeriesSpecification(specs));

		return series;
	}

	public String getVendorName() {
		return getClass().getSimpleName();
	}
}
