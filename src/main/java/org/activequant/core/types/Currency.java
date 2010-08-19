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
package org.activequant.core.types;


/**
 *  Symbols.<br>
 *  According to ISO 4217.<br>
 * <br>
 * <b>History:</b><br>
 *  - [13.07.2006] Created (Erik Nijkamp)<br>
 *
 *
 *  @author Erik Nijkamp
 */
public enum Currency {
    
    // Standard generic names
	AED(784,"United Arab Emirates dirham","United Arab Emirates",""),
	AFN(971,"Afghani","Afghanistan",""),
	ALL(8,"Lek","Albania",""),
	AMD(51,"Armenian Dram","Armenia",""),
	ANG(532,"Netherlands Antillian Guilder","Netherlands Antilles",""),
	AOA(973,"Kwanza","Angola",""),
	ARS(32,"Argentine Peso","Argentina",""),
	AUD(36,"Australian Dollar","Australia, Australian Antarctic Territory, Christmas Island, Cocos (Keeling) Islands, Heard and McDonald Islands, Kiribati, Nauru, Norfolk Island, Tuvalu",""),
	AWG(533,"Aruban Guilder","Aruba",""),
	AZN(944,"Azerbaijanian Manat","Azerbaijan",""),
	BAM(977,"Convertible Marks","Bosnia and Herzegovina",""),
	BBD(52,"Barbados Dollar","Barbados",""),
	BDT(50,"Bangladeshi Taka","Bangladesh",""),
	BGN(975,"Bulgarian Lev","Bulgaria",""),
	BHD(48,"Bahraini Dinar","Bahrain",""),
	BIF(108,"Burundian Franc","Burundi",""),
	BMD(60,"Bermudian Dollar (customarily known as Bermuda Dollar)","Bermuda",""),
	BND(96,"Brunei Dollar","Brunei",""),
	BOB(68,"Boliviano","Bolivia",""),
	BOV(984,"Bolivian Mvdol (Funds code)","Bolivia",""),
	BRL(986,"Brazilian Real","Brazil",""),
	BSD(44,"Bahamian Dollar","Bahamas",""),
	BTN(64,"Ngultrum","Bhutan",""),
	BWP(72,"Pula","Botswana",""),
	BYR(974,"Belarussian Ruble","Belarus",""),
	BZD(84,"Belize Dollar","Belize",""),
	CAD(124,"Canadian Dollar","Canada",""),
	CDF(976,"Franc Congolais","Democratic Republic of Congo",""),
	CHE(947,"WIR Euro (complementary currency)","Switzerland",""),
	CHF(756,"Swiss Franc","Switzerland, Liechtenstein",""),
	CHW(948,"WIR Franc (complementary currency)","Switzerland",""),
	CLF(990,"Unidades de formento (Funds code)","Chile",""),
	CLP(152,"Chilean Peso","Chile",""),
	CNY(156,"Yuan Renminbi","Mainland China",""),
	COP(170,"Colombian Peso","Colombia",""),
	COU(970,"Unidad de Valor Real","Colombia",""),
	CRC(188,"Costa Rican Colon","Costa Rica",""),
	CUP(192,"Cuban Peso","Cuba",""),
	CVE(132,"Cape Verde Escudo","Cape Verde",""),
	CYP(196,"Cyprus Pound","Cyprus",""),
	CZK(203,"Czech Koruna","Czech Republic",""),
	DJF(262,"Djibouti Franc","Djibouti",""),
	DKK(208,"Danish Krone","Denmark, Faroe Islands, Greenland",""),
	DOP(214,"Dominican Peso","Dominican Republic",""),
	DZD(12,"Algerian Dinar","Algeria",""),
	EEK(233,"Kroon","Estonia",""),
	EGP(818,"Egyptian Pound","Egypt",""),
	ERN(232,"Nakfa","Eritrea",""),
	ETB(230,"Ethiopian Birr","Ethiopia",""),
	EUR(978,"Euro","European Union, see eurozone",""),
	FJD(242,"Fiji Dollar","Fiji",""),
	FKP(238,"Falkland Islands Pound","Falkland Islands",""),
	GBP(826,"Pound Sterling","United Kingdom",""),
	GEL(981,"Lari","Georgia",""),
	GHS(288,"Cedi","Ghana",""),
	GIP(292,"Gibraltar pound","Gibraltar",""),
	GMD(270,"Dalasi","Gambia",""),
	GNF(324,"Guinea Franc","Guinea",""),
	GTQ(320,"Quetzal","Guatemala",""),
	GYD(328,"Guyana Dollar","Guyana",""),
	HKD(344,"Hong Kong Dollar","Hong Kong Special Administrative Region",""),
	HNL(340,"Lempira","Honduras",""),
	HRK(191,"Croatian Kuna","Croatia",""),
	HTG(332,"Haiti Gourde","Haiti",""),
	HUF(348,"Forint","Hungary",""),
	IDR(360,"Rupiah","Indonesia",""),
	ILS(376,"New Israeli Shekel","Israel",""),
	INR(356,"Indian Rupee","Bhutan, India",""),
	IQD(368,"Iraqi Dinar","Iraq",""),
	IRR(364,"Iranian Rial","Iran",""),
	ISK(352,"Iceland Krona","Iceland",""),
	JMD(388,"Jamaican Dollar","Jamaica",""),
	JOD(400,"Jordanian Dinar","Jordan",""),
	JPY(392,"Japanese yen","Japan",""),
	KES(404,"Kenyan Shilling","Kenya",""),
	KGS(417,"Som","Kyrgyzstan",""),
	KHR(116,"Riel","Cambodia",""),
	KMF(174,"Comoro Franc","Comoros",""),
	KPW(408,"North Korean Won","North Korea",""),
	KRW(410,"South Korean Won","South Korea",""),
	KWD(414,"Kuwaiti Dinar","Kuwait",""),
	KYD(136,"Cayman Islands Dollar","Cayman Islands",""),
	KZT(398,"Tenge","Kazakhstan",""),
	LAK(418,"Kip","Laos",""),
	LBP(422,"Lebanese Pound","Lebanon",""),
	LKR(144,"Sri Lanka Rupee","Sri Lanka",""),
	LRD(430,"Liberian Dollar","Liberia",""),
	LSL(426,"Loti","Lesotho",""),
	LTL(440,"Lithuanian Litas","Lithuania",""),
	LVL(428,"Latvian Lats","Latvia",""),
	LYD(434,"Libyan Dinar","Libya",""),
	MAD(504,"Moroccan Dirham","Morocco, Western Sahara",""),
	MDL(498,"Moldovan Leu","Moldova",""),
	MGA(969,"Malagasy Ariary","Madagascar",""),
	MKD(807,"Denar","Former Yugoslav Republic of Macedonia",""),
	MMK(104,"Kyat","Myanmar",""),
	MNT(496,"Tugrik","Mongolia",""),
	MOP(446,"Pataca","Macau Special Administrative Region",""),
	MRO(478,"Ouguiya","Mauritania",""),
	MTL(470,"Maltese Lira","Malta",""),
	MUR(480,"Mauritius Rupee","Mauritius",""),
	MVR(462,"Rufiyaa","Maldives",""),
	MWK(454,"Kwacha","Malawi",""),
	MXN(484,"Mexican Peso","Mexico",""),
	MXV(979,"Mexican Unidad de Inversion (UDI) (Funds code)","Mexico",""),
	MYR(458,"Malaysian Ringgit","Malaysia",""),
	MZN(943,"Metical","Mozambique",""),
	NAD(516,"Namibian Dollar","Namibia",""),
	NGN(566,"Naira","Nigeria",""),
	NIO(558,"Cordoba Oro","Nicaragua",""),
	NOK(578,"Norwegian Krone","Norway",""),
	NPR(524,"Nepalese Rupee","Nepal",""),
	NZD(554,"New Zealand Dollar","Cook Islands, New Zealand, Niue, Pitcairn, Tokelau",""),
	OMR(512,"Rial Omani","Oman",""),
	PAB(590,"Balboa","Panama",""),
	PEN(604,"Nuevo Sol","Peru",""),
	PGK(598,"Kina","Papua New Guinea",""),
	PHP(608,"Philippine Peso","Philippines",""),
	PKR(586,"Pakistan Rupee","Pakistan",""),
	PLN(985,"Zloty","Poland",""),
	PYG(600,"Guarani","Paraguay",""),
	QAR(634,"Qatari Rial","Qatar",""),
	RON(946,"Romanian New Leu","Romania",""),
	RSD(941,"Serbian Dinar","Serbia",""),
	RUB(643,"Russian Ruble","Russia, Abkhazia, South Ossetia",""),
	RWF(646,"Rwanda Franc","Rwanda",""),
	SAR(682,"Saudi Riyal","Saudi Arabia",""),
	SBD(90,"Solomon Islands Dollar","Solomon Islands",""),
	SCR(690,"Seychelles Rupee","Seychelles",""),
	SDG(938,"Sudanese Pound","Sudan",""),
	SEK(752,"Swedish Krona","Sweden",""),
	SGD(702,"Singapore Dollar","Singapore",""),
	SHP(654,"Saint Helena Pound","Saint Helena",""),
	SKK(703,"Slovak Koruna","Slovakia",""),
	SLL(694,"Leone","Sierra Leone",""),
	SOS(706,"Somali Shilling","Somalia",""),
	SRD(968,"Surinam Dollar","Suriname",""),
	STD(678,"Dobra","São Tomé and Príncipe",""),
	SYP(760,"Syrian Pound","Syria",""),
	SZL(748,"Lilangeni","Swaziland",""),
	THB(764,"Baht","Thailand",""),
	TJS(972,"Somoni","Tajikistan",""),
	TMM(795,"Manat","Turkmenistan",""),
	TND(788,"Tunisian Dinar","Tunisia",""),
	TOP(776,"Pa'anga","Tonga",""),
	TRY(949,"New Turkish Lira","Turkey",""),
	TTD(780,"Trinidad and Tobago Dollar","Trinidad and Tobago",""),
	TWD(901,"New Taiwan Dollar","Taiwan and other islands that are under the effective control of the Republic of China (ROC)",""),
	TZS(834,"Tanzanian Shilling","Tanzania",""),
	UAH(980,"Hryvnia","Ukraine",""),
	UGX(800,"Uganda Shilling","Uganda",""),
	USD(840,"US Dollar","American Samoa, British Indian Ocean Territory, Ecuador, El Salvador, Guam, Haiti, Marshall Islands, Micronesia, Northern Mariana Islands, Palau, Panama, Puerto Rico, East Timor, Turks and Caicos Islands, United States, Virgin Islands",""),
	USN(997,"United States dollar (Next day) (Funds code)","United States",""),
	USS(998,"United States dollar (Same day) (Funds code) (one source claims it is no longer used, but it is still on the ISO 4217-MA list)","United States",""),
	UYU(858,"Peso Uruguayo","Uruguay",""),
	UZS(860,"Uzbekistan Som","Uzbekistan",""),
	VEB(862,"Venezuelan bolívar","Venezuela",""),
	VND(704,"Vietnamese d?ng","Vietnam",""),
	VUV(548,"Vatu","Vanuatu",""),
	WST(882,"Samoan Tala","Samoa",""),
	XAF(950,"CFA Franc BEAC","Cameroon, Central African Republic, Congo, Chad, Equatorial Guinea, Gabon",""),
	XAG(961,"Silver (one Troy ounce)","0",""),
	XAU(959,"Gold (one Troy ounce)","0",""),
	XBA(955,"European Composite Unit (EURCO) (Bonds market unit)","0",""),
	XBB(956,"European Monetary Unit (E.M.U.-6) (Bonds market unit)","0",""),
	XBC(957,"European Unit of Account 9 (E.U.A.-9) (Bonds market unit)","0",""),
	XBD(958,"European Unit of Account 17 (E.U.A.-17) (Bonds market unit)","0",""),
	XCD(951,"East Caribbean Dollar","Anguilla, Antigua and Barbuda, Dominica, Grenada, Montserrat, Saint Kitts and Nevis, Saint Lucia, Saint Vincent and the Grenadines",""),
	XDR(960,"Special Drawing Rights","International Monetary Fund",""),
	XFO(1,"Gold franc (special settlement currency)","Bank for International Settlements",""),
	XFU(2,"UIC franc (special settlement currency)","International Union of Railways",""),
	XOF(952,"CFA Franc BCEAO","Benin, Burkina Faso, Côte d'Ivoire, Guinea-Bissau, Mali, Niger, Senegal, Togo",""),
	XPD(964,"Palladium (one Troy ounce)","0",""),
	XPF(953,"CFP franc","French Polynesia, New Caledonia, Wallis and Futuna",""),
	XPT(962,"Platinum (one Troy ounce)","0",""),
	XTS(963,"Code reserved for testing purposes","0",""),
	XXX(999,"No currency","0",""),
	YER(886,"Yemeni Rial","Yemen",""),
	ZAR(710,"South African Rand","South Africa",""),
	ZMK(894,"Kwacha","Zambia",""),
	ZWD(716,"Zimbabwe Dollar","Zimbabwe","");

	
	private int num;
	private String desc;
	private String location;
	private String symbol;
	
	Currency(int num, String desc, String location, String symbol) {
		this.num = num;
		this.desc = desc;
		this.location = location;
		this.symbol = symbol;
	}
	
	public String toString() {
		return name();
	}
	
	/**
	 * @return the symbol
	 */
	public String toSymbol() {
		return symbol;
	}
	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	
	public static Currency toClass(String value) {
		for(Currency symbol: values()) {
			if(symbol.desc.equals(value)) {
				return symbol;
			}
		}
		throw new IllegalArgumentException("Value '"+value+"' not defined as constant.");
	}

}