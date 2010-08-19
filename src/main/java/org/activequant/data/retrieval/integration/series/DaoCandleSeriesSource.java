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
package org.activequant.data.retrieval.integration.series;

import org.activequant.core.domainmodel.CandleSeries;
import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.dao.ICandleDao;
import org.activequant.data.retrieval.integration.CandleSeriesSourceBase;
import org.apache.log4j.Logger;

/**
 * @TODO<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.05.2007] Created (Ulrich Staudinger)<br>
 *  - [07.05.2007] Polished (Erik Nijkamp)<br>
 *  - [10.05.2007] Removed cryptic OHLC set, adding skip rows again, fixing (Ulrich Staudinger)<br>
 *  - [11.08.2007] Added array functions (Erik Nijkamp)<br>
 *  - [25.09.2007] Switch to InstrumentQuery etc. (Erik Nijkamp)<br>
 *  - [18.10.2007] Cleanup, removed unnecessary methods. (Ulrich Staudinger)<br>
 *  - [09.11.2007] Moved to new data interfaces (Erik Nijkamp)<br>
 *    
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class DaoCandleSeriesSource extends CandleSeriesSourceBase {

    protected final static Logger log = Logger.getLogger(DaoCandleSeriesSource.class);

    private ICandleDao candleDao = null;

    public DaoCandleSeriesSource(ICandleDao candleDao){
    	this.candleDao = candleDao;
    }

	public CandleSeries fetch(InstrumentSpecification spec) throws Exception {
		return new CandleSeries(candleDao.findByInstrumentSpecification(spec));
	}
	
	public CandleSeries fetch(SeriesSpecification spec) throws Exception {
		CandleSeries series = new CandleSeries(candleDao.findBySeriesSpecification(spec));
		series.setSeriesSpecification(spec);
		return series;
	}

    public String getVendorName(){
        return "DAO Layer";
    }
}