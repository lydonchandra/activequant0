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
package org.activequant.dao;

import org.activequant.core.domainmodel.InstrumentSpecification;
import org.activequant.core.domainmodel.MarketDataEntity;
import org.activequant.core.domainmodel.SeriesSpecification;
import org.activequant.util.exceptions.DaoException;


/**
 * Extends the default dao support with series-specific functionality.<br>
 * <br>
 * <b>History:</b><br>
 *  - [25.08.2007] Created [Based on former dao2/ibatis/CandleSeriesDao code by Ulrich Staudinger] (Erik Nijkamp)<br>
 *  - [13.10.2007] Cleanup of method names and move to communicated domain model (Ulrich Staudinger)<br>
 *
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 */
public interface IMarketDataEntityDao<T extends MarketDataEntity> extends IDaoSupport<T> {
	
    /**
     * Returns a set of elements for a given instrument.
     * @param instrumentSpecification
     * @return
     * @throws DaoException
     */
	T[] findByInstrumentSpecification(InstrumentSpecification instrumentSpecification) throws DaoException;
	
	/**
	 * Returns a set of elements for a given query.
	 * @param seriesSpecification
	 * @return
	 * @throws DaoException
	 */
	T[] findBySeriesSpecification(SeriesSpecification seriesSpecification) throws DaoException;
	
    /**
     * Deletes a set of elements for a given instrument.
     * @param instrumentSpecification
     * @throws DaoException
     */
	void deleteByInstrumentSpecification(InstrumentSpecification instrumentSpecification) throws DaoException;
	
	/**
	 * Deletes a set of elements for a given query.
	 * @param seriesSpecification
	 * @throws DaoException
	 */
	void deleteBySeriesSpecification(SeriesSpecification seriesSpecification) throws DaoException;

}
