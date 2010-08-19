/************************************************************
 * 
 * CCAPI. 2001ff, activestocks.de / Ulrich Staudinger
 * @license    GPL 2 (http://www.gnu.org/licenses/gpl.html) 
 * 
 ************************************************************/
package org.activequant.data.retrieval;

import org.activequant.core.domainmodel.CandleSeries;

/**
 * Interface for the sources that can deliver time series information.<br>
 * <br>  
 * <b>History:</b><br>
 *  - [16.04.2006] Created (Ulrich Staudinger)<br>
 *  - [04.05.2007] Refactoring (Erik Nijkamp)<br>
 *  - [14.08.2007] Added Array method (Ulrich Staudinger)<br>
 *  - [25.09.2007] Switch to InstrumentQuery etc. (Erik Nijkamp)<br>
 *  - [28.09.2007] Moved to new domain model (Erik Nijkamp)<br>
 *  - [17.10.2007] Added generic super interface (Erik Nijkamp)<br>
 *  
 *  @author Erik Nijkamp
 *  @author Ulrich Staudinger
 *
 */
public interface ICandleSeriesSource extends ISeriesDataSource<CandleSeries> {
	
}