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
package org.activequant.core.domainmodel;

import static org.activequant.util.tools.IdentityUtils.equalsTo;
import static org.activequant.util.tools.IdentityUtils.safeCompare;
import static org.activequant.util.tools.IdentityUtils.safeHashCode;

import org.activequant.core.types.Currency;
import org.activequant.core.types.Exchange;
import org.activequant.core.types.Expiry;
import org.activequant.core.types.SecurityType;
import org.activequant.util.tools.DecorationsMap;


/**
 * Specifies an instrument.
 * <br>
 * <em>IMPORTANT</em>: This object has "value" identity. 
 * It means that its identity
 * (hashCode, equals, and comapreTo) is determined by subset of the fields,
 * declared "business keys". Other fields are considered a "payload".
 * This identity reflects the suggested unique key constraints in the
 * related sql table.
 * <em>IMPORTANT</em>: If this object is added to a collection that relies on
 * identity, changing business key will break the collection 
 * contract and will cause unexpected results.
 * <br>
 * Business keys for InstrumentSpecification are:
 * <ul>
 * <li><code>{@link #getSymbol() symbol}</code>
 * <li><code>{@link #getExchange() exchange}</code>
 * <li><code>{@link #getVendor() vendor}</code>
 * <li><code>{@link #getCurrency() currency}</code>
 * <li><code>{@link #getSecurityType() securityType}</code>
 * <li><code>{@link #getExpiry() expiry}</code>
 * <li><code>{@link #getStrike() strike}</code>
 * <li><code>{@link #getContractRight() contractRight}</code>
 * </ul>
 * <br>
 * <b>History:</b><br>
 *  - [16.06.2007] Created (Ulrich Staudinger)<br>
 *  - [20.06.2007] Added additional constructor and decorations map (Erik Nijkamp)<br>
 *  - [07.10.2007] Added proper object identity code (hashcode, equals) (Erik Nijkamp)<br>
 *  - [08.10.2007] Made it Comparable, aligned identity with comparable (Mike Kroutikov)<br>
 *  - [10.10.2007] Changing expiry to date. (Ulrich Staudinger)<br>
 *  - [18.10.2007] Fixing to file name method. (Ulrich Staudinger)<br>
 *  - [23.10.2007] Fixing expiry constructors. (Ulrich Staudinger)<br> 
 *  - [24.10.2007] Fixing expiry constructors + fixed equals (Erik Nijkamp)<br>
 *  - [29.10.2007] Minor constructor fixes (Erik Nijkamp)<br> 
 *
 *  @author Ulrich Staudinger
 *  @author Erik Nijkamp
 */
public class InstrumentSpecification implements Comparable<InstrumentSpecification> {
	
	public static final int NOT_SET = -1;
	
	private Long id;
	private Symbol symbol = null;
	private String exchange = null;
	private String vendor = null;
	private String currency = null;      // TODO use enums rc3 (en)
	private String securityType = null;  // TODO use enums rc3 (en)
	private Expiry expiry = null;

	/**
	 * in case of an option, this is the strike
	 */
	private double strike = NOT_SET; 
	/**
	 * in case of an option, this is the right (Call or put or something else)
	 */
	private String contractRight = null;
	
	/**
	 * can contain decorations, these are contrary to additional information.
	 */
	private DecorationsMap decorations = new DecorationsMap(); 
	
	public InstrumentSpecification() {
		
	}
	
	public InstrumentSpecification(Symbol symbol){
		this.symbol = symbol;
	}

	/**
	 * copy constructor.
	 * @param instrumentSpecification
	 */
	public InstrumentSpecification(InstrumentSpecification spec) {
		this.id = spec.getId();
		this.symbol = spec.getSymbol();
		this.exchange = spec.getExchange();
		this.vendor = spec.getVendor();
		this.currency = spec.getCurrency();
		this.securityType = spec.getSecurityType();
		this.expiry = spec.getExpiry();
		this.decorations = spec.getDecorationMap();
		this.contractRight = spec.getContractRight();
		this.strike = spec.getStrike();
	}
	
	
	/**
	 * Using symbol.
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 */
	public InstrumentSpecification(Symbol symbol, String exchange, String vendor,
			String currency, String securityType) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
	}
	
	/**
	 * Using symbol.
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 * @param expiry
	 */
	public InstrumentSpecification(Symbol symbol, String exchange, String vendor,
			String currency, String securityType, Expiry expiry) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
		this.expiry = expiry;
	}
	
	/**
	 * 
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 * @param expiry
	 * @param infos
	 */
	public InstrumentSpecification(Symbol symbol, String exchange, String vendor,
			String currency, String securityType, Expiry expiry, String... infos) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
		this.expiry = expiry;
		if(infos != null)
			decorations.addDecorations(infos);
	}
	
	/**
	 * 
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 * @param expiry
	 * @param infos
	 */
	public InstrumentSpecification(Symbol symbol, String exchange, String vendor,
			String currency, String securityType, Expiry expiry, String infos) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
		this.expiry = expiry;
		decorations.addDecorations(infos);
	}

	/**
	 * 
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 * @param expiry
	 * @param strike
	 * @param contractRight
	 */
	public InstrumentSpecification(Symbol symbol, String exchange,
			String vendor, String currency, String securityType, Expiry expiry,
			double strike, String contractRight) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
		if(expiry!=null){
			this.expiry = new Expiry(expiry);
		}
		this.strike = strike;
		this.contractRight = contractRight;
	}
	
	/**
	 * 
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 * @param expiry
	 * @param strike
	 * @param contractRight
	 * @param infos
	 */
	public InstrumentSpecification(Symbol symbol, String exchange,
			String vendor, String currency, String securityType, Expiry expiry,
			double strike, String contractRight, String infos) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
		this.expiry = expiry;
		this.strike = strike;
		this.contractRight = contractRight;
		decorations.addDecorations(infos);
	}
	
	/**
	 * 
	 * @param symbol
	 * @param exchange
	 * @param vendor
	 * @param currency
	 * @param securityType
	 * @param expiry
	 * @param strike
	 * @param contractRight
	 * @param decorations
	 */
	public InstrumentSpecification(Symbol symbol, String exchange,
			String vendor, String currency, String securityType, Expiry expiry,
			double strike, String contractRight, DecorationsMap decorations) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.vendor = vendor;
		this.currency = currency;
		this.securityType = securityType;
		this.expiry = expiry;
		this.strike = strike;
		this.contractRight = contractRight;
		this.decorations = decorations;
	}
	
    /**
     * clone this instance.
     * @return cloned object
     */
    @Override
	public InstrumentSpecification clone() {
		return new InstrumentSpecification(
				symbol,
				exchange,
				vendor,
				currency,
				securityType,
				expiry,
				strike,
				contractRight,
				decorations);
	}
	
    /**
     * return object's state as string.
     * @return text
     */
    @Override
    public String toString() {
		return id + "\t" + symbol + "\t" + exchange + "\t" + vendor + "\t" + currency
				+ "\t" + securityType + "\t" + expiry + "\t" + contractRight
				+ "\t" + strike;
	}
    
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * assigned id?
	 * @return
	 */
	public boolean hasId() {
		return id != null;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public void setCurrencyEnum(Currency currency) {
		this.currency = currency.toString();
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	
	public void setExchangeEnum(Exchange exchange) {
		this.exchange = exchange.toString();
	}
	
	public void setExpiry(Expiry expiry) {
		this.expiry = expiry;
	}
	
	public Expiry getExpiry() {
		return expiry;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityTypeEnum(SecurityType securityType) {
		this.securityType = securityType.toString();
	}
	
	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}	
	
	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public boolean hasStrike() {
		return strike != NOT_SET;
	}
	
	public double getStrike() {
		return strike;
	}

	public void setStrike(double strike) {
		this.strike = strike;
	}
	
    public DecorationsMap getDecorationMap() {
        return decorations;
    }
    
    public void setDecorationMap(DecorationsMap decorations) {
        this.decorations = decorations;
    }
    
    public <T> T getDecoration(String key) {
    	return decorations.<T>get(key);
    }
    
    public boolean containsDecoration(String key) {
    	return decorations.containsKey(key);
    }
    
    public <T> void addDecoration(String key, T value) {
    	decorations.put(key, value);
    }
    
	public boolean hasContractRight() {
		return contractRight != null;
	}

	public String getContractRight() {
		return contractRight;
	}

	public void setContractRight(String contractRight) {
		this.contractRight = contractRight;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		// ATTENTION: keep in sync with compareTo()
		return safeHashCode(this.symbol) 
			+ safeHashCode(this.exchange)
			+ safeHashCode(this.vendor)
			+ safeHashCode(this.currency)
			+ safeHashCode(this.securityType)
			+ safeHashCode(this.expiry)
			+ safeHashCode(this.strike)
			+ safeHashCode(this.contractRight);
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(InstrumentSpecification other) {
		// ATTENTION: keep in sync with hashCode();
		int rc;
		
		rc = safeCompare(this.symbol, other.symbol);
		if(rc != 0) return rc;
		rc = safeCompare(this.exchange, other.exchange);
		if(rc != 0) return rc;
		rc = safeCompare(this.vendor, other.vendor);
		if(rc != 0) return rc;
		rc = safeCompare(this.currency, other.currency);
		if(rc != 0) return rc;
		rc = safeCompare(this.securityType, other.securityType);
		if(rc != 0) return rc;
		rc = safeCompare(this.expiry, other.expiry);
		if(rc != 0) return rc;
		rc = safeCompare(this.strike, other.strike);
		if(rc != 0) return rc;
		rc = safeCompare(this.contractRight, other.contractRight);
		if(rc != 0) return rc;
		
		return rc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		// NOTE: delegates to compareTo()
		return equalsTo(this, other);
	}
}
