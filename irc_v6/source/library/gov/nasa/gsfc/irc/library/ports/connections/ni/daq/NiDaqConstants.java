//=== File Prolog ============================================================
//	This code was developed by NASA, Goddard Space Flight Center, Code 580
//	for the Instrument Remote Control (IRC) project.
//
//--- Notes ------------------------------------------------------------------
//	This class requires JDK 1.1 or later.
//
//--- Development History  ---------------------------------------------------
//
//  $Log: 
//   1    IRC       1.0         4/2/2001 9:58:00 AM  Troy Ames       
//  $
//	06/28/99	T. Ames/588
//		Initial version.
//
//--- Warning ----------------------------------------------------------------
//	This software is property of the National Aeronautics and Space
//	Administration. Unauthorized use or duplication of this software is
//	strictly prohibited. Authorized users are subject to the following
//	restrictions:
//	*	Neither the author, their corporation, nor NASA is responsible for
//		any consequence of the use of this software.
//	*	The origin of this software must not be misrepresented either by
//		explicit claim or by omission.
//	*	Altered versions of this software must be plainly marked as such.
//	*	This notice may not be removed or altered.
//
//=== End File Prolog ========================================================

package gov.nasa.gsfc.irc.library.ports.connections.ni.daq;

/**
 *  This file contains definitions for constants required for some
 *	of the NI-DAQ functions. NI-DAQ is a set of functions
 *	that control all of the National Instruments
 *	plug-in DAQ devices for analog I/O, digital I/O, timing I/O, SCXI signal
 *	conditioning, and RTSI multiboard synchronization. Refer to the documents:
 *	<i><a href="ftp://ftp.natinst.com/support/manuals/321644d.pdf">NI-DAQ User Manual</a></i>
 *	and <i><a href="ftp://ftp.natinst.com/support/manuals/321645d.pdf">NI-DAQ Function Reference Manual</a></i>
 *	available from <a href="http://www.natinst.com/">National Instruments</a>.
 *
 *
 *  <P>This code was developed for NASA, Goddard Space Flight Center, Code 580
 *  for the Instrument Remote Control (IRC) project.
 *
 *  @version	$Date: 2004/08/23 17:52:12 $
 *  @author		T. Ames
**/

public interface NiDaqConstants
{
	public static final int ND_ABOVE_HIGH_LEVEL          =	11020;
	public static final int ND_AC                        =	11025;
	public static final int ND_ACK_REQ_EXCHANGE_GR1      =	11030;
	public static final int ND_ACK_REQ_EXCHANGE_GR2      =	11035;
	public static final int ND_ACTIVE                    =	11037;
	public static final int ND_ADC_RESOLUTION            =	11040;
	public static final int ND_AI_CALDAC_COUNT           =	11050;
	public static final int ND_AI_CHANNEL_COUNT          =	11060;
	public static final int ND_AI_COUPLING               =	11055;
	public static final int ND_AI_FIFO_INTERRUPTS        =	11600;
	public static final int ND_ANALOG_FILTER             =	11065;
	public static final int ND_AO48XDC_SET_POWERUP_STATE =	42100;
	public static final int ND_AO_CALDAC_COUNT           =	11070;
	public static final int ND_AO_CHANNEL_COUNT          =	11080;
	public static final int ND_AO_EXT_REF_CAPABLE        =	11090;
	public static final int ND_AO_UNIPOLAR_CAPABLE       =	11095;
	public static final int ND_ARM                       =	11100;
	public static final int ND_ARMED                     =	11200;
	public static final int ND_ATC_OUT                   =	11250;
	public static final int ND_ATTENUATION               =	11260;
	public static final int ND_AUTOINCREMENT_COUNT       =	11300;
	public static final int ND_AUTOMATIC                 =	11400;
	public static final int ND_AVAILABLE_POINTS          =	11500;

	public static final int ND_BASE_ADDRESS              =	12100;
	public static final int ND_BELOW_LOW_LEVEL           =	12130;
	public static final int ND_BOARD_CLOCK               =	12170;
	public static final int ND_BUFFERED_EVENT_CNT        =	12200;
	public static final int ND_BUFFERED_PERIOD_MSR       =	12300;
	public static final int ND_BUFFERED_PULSE_WIDTH_MSR  =	12400;
	public static final int ND_BUFFERED_SEMI_PERIOD_MSR  =	12500;
	public static final int ND_BURST                     =	12600;
	public static final int ND_BURST_INTERVAL            =	12700;

	public static final int ND_CAL_CONST_AUTO_LOAD       =	13050;
	public static final int ND_CALIBRATION_FRAME_SIZE    =	13060;
	public static final int ND_CALIBRATION_FRAME_PTR     =	13065;
	public static final int ND_CJ_TEMP                   =	((short)(0x8000));
	public static final int ND_CALGND                    =	((short)(0x8001));
	public static final int ND_CLEAN_UP                  =	13100;
	public static final int ND_CLOCK_REVERSE_MODE_GR1    =	13120;
	public static final int ND_CLOCK_REVERSE_MODE_GR2    =	13130;
	public static final int ND_CONFIG_MEMORY_SIZE        =	13150;
	public static final int ND_CONTINUOUS                =	13160;
	public static final int ND_COUNT                     =	13200;

	public static final int ND_COUNTER_0                 =	13300;
	public static final int ND_COUNTER_1                 =	13400;
	public static final int ND_COUNTER_2                 =	13310;
	public static final int ND_COUNTER_3                 =	13320;
	public static final int ND_COUNTER_4                 =	13330;
	public static final int ND_COUNTER_5                 =	13340;
	public static final int ND_COUNTER_6                 =	13350;
	public static final int ND_COUNTER_7                 =	13360;

	public static final int ND_COUNTER_1_SOURCE          =	13430;
	public static final int ND_COUNT_AVAILABLE           =	13450;
	public static final int ND_COUNT_DOWN                =	13465;
	public static final int ND_COUNT_UP                  =	13485;
	public static final int ND_COUNT_1                   =	13500;
	public static final int ND_COUNT_2                   =	13600;
	public static final int ND_COUNT_3                   =	13700;
	public static final int ND_COUNT_4                   =	13800;
	public static final int ND_CURRENT_OUTPUT            =	40200;

	public static final int ND_DAC_RESOLUTION            =	13950;
	public static final int ND_DATA_TRANSFER_CONDITION   =	13960;
	public static final int ND_DATA_XFER_MODE_AI         =	14000;
	public static final int ND_DATA_XFER_MODE_AO_GR1     =	14100;
	public static final int ND_DATA_XFER_MODE_AO_GR2     =	14200;
	public static final int ND_DATA_XFER_MODE_DIO_GR1    =	14300;
	public static final int ND_DATA_XFER_MODE_DIO_GR2    =	14400;
	public static final int ND_DATA_XFER_MODE_DIO_GR3    =	14500;
	public static final int ND_DATA_XFER_MODE_DIO_GR4    =	14600;
	public static final int ND_DATA_XFER_MODE_DIO_GR5    =	14700;
	public static final int ND_DATA_XFER_MODE_DIO_GR6    =	14800;
	public static final int ND_DATA_XFER_MODE_DIO_GR7    =	14900;
	public static final int ND_DATA_XFER_MODE_DIO_GR8    =	15000;

	public static final int ND_DATA_XFER_MODE_GPCTR0     =	15100;
	public static final int ND_DATA_XFER_MODE_GPCTR1     =	15200;
	public static final int ND_DATA_XFER_MODE_GPCTR2     =	15110;
	public static final int ND_DATA_XFER_MODE_GPCTR3     =	15120;
	public static final int ND_DATA_XFER_MODE_GPCTR4     =	15130;
	public static final int ND_DATA_XFER_MODE_GPCTR5     =	15140;
	public static final int ND_DATA_XFER_MODE_GPCTR6     =	15150;
	public static final int ND_DATA_XFER_MODE_GPCTR7     =	15160;

	public static final int ND_DC                        =	15250;
	public static final int ND_DEVICE_NAME               =	15260;
	public static final int ND_DEVICE_POWER              =	15270;
	public static final int ND_DEVICE_SERIAL_NUMBER      =	15280;
	public static final int ND_DEVICE_STATE_DURING_SUSPEND_MODE	= 15290;
	public static final int ND_DEVICE_TYPE_CODE          =	15300;
	public static final int ND_DIGITAL_FILTER            =	15350;
	public static final int ND_DIGITAL_RESTART           =	15375;
	public static final int ND_DIO128_GET_PORT_THRESHOLD =	41200;
	public static final int ND_DIO128_SELECT_INPUT_PORT  =	41100;
	public static final int ND_DIO128_SET_PORT_THRESHOLD =	41300;
	public static final int ND_DISABLED                  =	15400;
	public static final int ND_DISARM                    =	15450;
	public static final int ND_DMA_A_LEVEL               =	15500;
	public static final int ND_DMA_B_LEVEL               =	15600;
	public static final int ND_DMA_C_LEVEL               =	15700;
	public static final int ND_DONE                      =	15800;
	public static final int ND_DONT_CARE                 =	15900;
	public static final int ND_DONT_KNOW                 =	15950;

	public static final int ND_EDGE_SENSITIVE            =	16000;
	public static final int ND_ENABLED                   =	16050;
	public static final int ND_END                       =	16055;
	public static final int ND_EXTERNAL                  =	16060;
	public static final int ND_EXTERNAL_CALIBRATE        =	16100;

	public static final int ND_FACTORY_CALIBRATION_DATE  =	16200;
	public static final int ND_FACTORY_CALIBRATION_EQUIP =	16210;
	public static final int ND_FACTORY_EEPROM_AREA       =	16220;
	public static final int ND_FIFO_EMPTY                =	16230;
	public static final int ND_FIFO_HALF_FULL_OR_LESS    =	16240;
	public static final int ND_FIFO_HALF_FULL_OR_LESS_UNTIL_FULL	= 16245;
	public static final int ND_FIFO_NOT_FULL             =	16250;
	public static final int ND_FIFO_TRANSFER_COUNT       =	16260;
	public static final int ND_FILTER_CORRECTION_FREQ    =	16300;
	public static final int ND_FOREGROUND                =	16350;
	public static final int ND_FREQ_OUT                  =	16400;
	public static final int ND_FSK                       =	16500;

	public static final int ND_GATE                      =	17100;
	public static final int ND_GATE_POLARITY             =	17200;

	public static final int ND_GPCTR0_GATE               =	17300;
	public static final int ND_GPCTR0_OUTPUT             =	17400;
	public static final int ND_GPCTR0_SOURCE             =	17500;

	public static final int ND_GPCTR1_GATE               =	17600;
	public static final int ND_GPCTR1_OUTPUT             =	17700;
	public static final int ND_GPCTR1_SOURCE             =	17800;

	public static final int ND_GPCTR2_GATE               =	17320;
	public static final int ND_GPCTR2_OUTPUT             =	17420;
	public static final int ND_GPCTR2_SOURCE             =	17520;

	public static final int ND_GPCTR3_GATE               =	17330;
	public static final int ND_GPCTR3_OUTPUT             =	17430;
	public static final int ND_GPCTR3_SOURCE             =	17530;

	public static final int ND_GPCTR4_GATE               =	17340;
	public static final int ND_GPCTR4_OUTPUT             =	17440;
	public static final int ND_GPCTR4_SOURCE             =	17540;

	public static final int ND_GPCTR5_GATE               =	17350;
	public static final int ND_GPCTR5_OUTPUT             =	17450;
	public static final int ND_GPCTR5_SOURCE             =	17550;

	public static final int ND_GPCTR6_GATE               =	17360;
	public static final int ND_GPCTR6_OUTPUT             =	17460;
	public static final int ND_GPCTR6_SOURCE             =	17660;

	public static final int ND_GPCTR7_GATE               =	17370;
	public static final int ND_GPCTR7_OUTPUT             =	17470;
	public static final int ND_GPCTR7_SOURCE             =	17570;

	public static final int ND_GROUND_DAC_REFERENCE      =	17900;

	public static final int ND_HARDWARE                  =	18000;
	public static final int ND_HIGH                      =	18050;
	public static final int ND_HIGH_HYSTERESIS           =	18080;
	public static final int ND_HIGH_TO_LOW               =	18100;
	public static final int ND_HW_ANALOG_TRIGGER         =	18900;

	public static final int ND_IMPEDANCE                 =	19000;
	public static final int ND_INACTIVE                  =	19010;
	public static final int ND_INITIAL_COUNT             =	19100;
	public static final int ND_INIT_PLUGPLAY_DEVICES     =	19110;
	public static final int ND_INSIDE_REGION             =	19150;
	public static final int ND_INTERNAL                  =	19160;
	public static final int ND_INTERNAL_100_KHZ          =	19200;
	public static final int ND_INTERNAL_10_MHZ           =	19300;
	public static final int ND_INTERNAL_1250_KHZ         =	19320;
	public static final int ND_INTERNAL_20_MHZ           =	19400;
	public static final int ND_INTERNAL_2500_KHZ         =	19420;
	public static final int ND_INTERNAL_7160_KHZ         =	19460;
	public static final int ND_INTERNAL_TIMER            =	19500;
	public static final int ND_INTERRUPTS                =	19600;
	public static final int ND_INTERRUPT_A_LEVEL         =	19700;
	public static final int ND_INTERRUPT_B_LEVEL         =	19800;
	public static final int ND_INTERRUPT_TRIGGER_MODE    =	19850;
	public static final int ND_IN_CHANNEL_CLOCK_TIMEBASE =	19900;
	public static final int ND_IN_CHANNEL_CLOCK_TB_POL   =	20000;
	public static final int ND_IN_CONVERT                =	20100;
	public static final int ND_IN_CONVERT_POL            =	20200;
	public static final int ND_IN_DATA_FIFO_SIZE         =	20250;
	public static final int ND_IN_EXTERNAL_GATE          =	20300;
	public static final int ND_IN_EXTERNAL_GATE_POL      =	20400;
	public static final int ND_IN_SCAN_CLOCK_TIMEBASE    =	20500;
	public static final int ND_IN_SCAN_CLOCK_TB_POL      =	20600;
	public static final int ND_IN_SCAN_IN_PROG           =	20650;
	public static final int ND_IN_SCAN_START             =	20700;
	public static final int ND_IN_SCAN_START_POL         =	20800;
	public static final int ND_IN_START_TRIGGER          =	20900;
	public static final int ND_IN_START_TRIGGER_POL      =	21000;
	public static final int ND_IN_STOP_TRIGGER           =	21100;
	public static final int ND_IN_STOP_TRIGGER_POL       =	21200;
	public static final int ND_INT_AI_GND                =	21210;
	public static final int ND_INT_AO_CH_0               =	21230;
	public static final int ND_INT_AO_CH_0_VS_REF_5V     =	21235;
	public static final int ND_INT_AO_CH_1               =	21240;
	public static final int ND_INT_AO_CH_1_VS_AO_CH_0    =	21245;
	public static final int ND_INT_AO_CH_1_VS_REF_5V     =	21250;
	public static final int ND_INT_AO_GND                =	21260;
	public static final int ND_INT_AO_GND_VS_AI_GND      =	21265;
	public static final int ND_INT_CM_REF_5V             =	21270;
	public static final int ND_INT_DEV_TEMP              =	21280;
	public static final int ND_INT_REF_5V                =	21290;
	public static final int ND_INT_CAL_BUS               =	21295;
	public static final int ND_INT_MUX_BUS               =	21305;

	public static final int ND_INT_AI_GND_AMP_0          =	21211;
	public static final int ND_INT_AI_GND_AMP_1          =	21212;
	public static final int ND_INT_AI_GND_AMP_2          =	21213;
	public static final int ND_INT_AI_GND_AMP_3          =	21214;
	public static final int ND_INT_AO_CH_0_AMP_0         =	21231;
	public static final int ND_INT_AO_CH_0_AMP_1         =	21232;
	public static final int ND_INT_AO_CH_0_AMP_2         =	21233;
	public static final int ND_INT_AO_CH_0_AMP_3         =	21234;
	public static final int ND_INT_AO_CH_1_AMP_0         =	21241;
	public static final int ND_INT_AO_CH_1_AMP_1         =	21242;
	public static final int ND_INT_AO_CH_1_AMP_2         =	21243;
	public static final int ND_INT_AO_CH_1_AMP_3         =	21244;
	public static final int ND_INT_AO_CH_0_VS_REF_AMP_0  =	21236;
	public static final int ND_INT_AO_CH_0_VS_REF_AMP_1  =	21237;
	public static final int ND_INT_AO_CH_0_VS_REF_AMP_2  =	21238;
	public static final int ND_INT_AO_CH_0_VS_REF_AMP_3  =	21239;
	public static final int ND_INT_AO_CH_1_VS_REF_AMP_0  =	21251;
	public static final int ND_INT_AO_CH_1_VS_REF_AMP_1  =	21252;
	public static final int ND_INT_AO_CH_1_VS_REF_AMP_2  =	21253;
	public static final int ND_INT_AO_CH_1_VS_REF_AMP_3  =	21254;
	public static final int ND_INT_AO_GND_VS_AI_GND_AMP_0=	21266;
	public static final int ND_INT_AO_GND_VS_AI_GND_AMP_1=	21267;
	public static final int ND_INT_AO_GND_VS_AI_GND_AMP_2=	21268;
	public static final int ND_INT_AO_GND_VS_AI_GND_AMP_3=	21269;
	public static final int ND_INT_CM_REF_AMP_0          =	21271;
	public static final int ND_INT_CM_REF_AMP_1          =	21272;
	public static final int ND_INT_CM_REF_AMP_2          =	21273;
	public static final int ND_INT_CM_REF_AMP_3          =	21274;
	public static final int ND_INT_REF_AMP_0             =	21291;
	public static final int ND_INT_REF_AMP_1             =	21292;
	public static final int ND_INT_REF_AMP_2             =	21293;
	public static final int ND_INT_REF_AMP_3             =	21294;

	public static final int ND_INTERRUPT_EVERY_SAMPLE    =	11700;
	public static final int ND_INTERRUPT_HALF_FIFO       =	11800;
	public static final int ND_IO_CONNECTOR              =	21300;

	public static final int ND_LEVEL_SENSITIVE           =	24000;
	public static final int ND_LINK_COMPLETE_INTERRUPTS  =	24010;
	public static final int ND_LOW                       =	24050;
	public static final int ND_LOW_HYSTERESIS            =	24080;
	public static final int ND_LOW_TO_HIGH               =	24100;
	public static final int ND_LPT_DEVICE_MODE           =	24200;

	public static final int ND_MARKER                    =	24500;
	public static final int ND_MEMORY_TRANSFER_WIDTH     =	24600;

	public static final int ND_NEGATIVE                  =	26100;
	public static final int ND_NEW                       =	26190;
	public static final int ND_NI_DAQ_SW_AREA            =	26195;
	public static final int ND_NO                        =	26200;
	public static final int ND_NO_STRAIN_GAUGE           =	26225;
	public static final int ND_NO_TRACK_AND_HOLD         =	26250;
	public static final int ND_NONE                      =	26300;
	public static final int ND_NOT_APPLICABLE            =	26400;
	public static final int ND_NUMBER_DIG_PORTS          =	26500;

	public static final int ND_OFF                       =	27010;
	public static final int ND_OFFSET                    =	27020;
	public static final int ND_ON                        =	27050;
	public static final int ND_OTHER                     =	27060;
	public static final int ND_OTHER_GPCTR_OUTPUT        =	27300;
	public static final int ND_OTHER_GPCTR_TC            =	27400;
	public static final int ND_OUT_DATA_FIFO_SIZE        =	27070;
	public static final int ND_OUT_EXTERNAL_GATE         =	27080;
	public static final int ND_OUT_EXTERNAL_GATE_POL     =	27082;
	public static final int ND_OUT_START_TRIGGER         =	27100;
	public static final int ND_OUT_START_TRIGGER_POL     =	27102;
	public static final int ND_OUT_UPDATE                =	27200;
	public static final int ND_OUT_UPDATE_POL            =	27202;
	public static final int ND_OUT_UPDATE_CLOCK_TIMEBASE =	27210;
	public static final int ND_OUT_UPDATE_CLOCK_TB_POL   =	27212;
	public static final int ND_OUTPUT_ENABLE             =	27220;
	public static final int ND_OUTPUT_MODE               =	27230;
	public static final int ND_OUTPUT_POLARITY           =	27240;
	public static final int ND_OUTPUT_STATE              =	27250;
	public static final int ND_OUTPUT_TYPE               =	40000;

	public static final int ND_DIGITAL_PATTERN_GENERATION=	28030;
	public static final int ND_PAUSE                     =	28040;
	public static final int ND_PAUSE_ON_HIGH             =	28045;
	public static final int ND_PAUSE_ON_LOW              =	28050;
	public static final int ND_PFI_0                     =	28100;
	public static final int ND_PFI_1                     =	28200;
	public static final int ND_PFI_2                     =	28300;
	public static final int ND_PFI_3                     =	28400;
	public static final int ND_PFI_4                     =	28500;
	public static final int ND_PFI_5                     =	28600;
	public static final int ND_PFI_6                     =	28700;
	public static final int ND_PFI_7                     =	28800;
	public static final int ND_PFI_8                     =	28900;
	public static final int ND_PFI_9                     =	29000;
	public static final int ND_PFI_10                    =	50280;
	public static final int ND_PFI_11                    =	50290;
	public static final int ND_PFI_12                    =	50300;
	public static final int ND_PFI_13                    =	50310;
	public static final int ND_PFI_14                    =	50320;
	public static final int ND_PFI_15                    =	50330;
	public static final int ND_PFI_16                    =	50340;
	public static final int ND_PFI_17                    =	50350;
	public static final int ND_PFI_18                    =	50360;
	public static final int ND_PFI_19                    =	50370;
	public static final int ND_PFI_20                    =	50380;
	public static final int ND_PFI_21                    =	50390;
	public static final int ND_PFI_22                    =	50400;
	public static final int ND_PFI_23                    =	50410;
	public static final int ND_PFI_24                    =	50420;
	public static final int ND_PFI_25                    =	50430;
	public static final int ND_PFI_26                    =	50440;
	public static final int ND_PFI_27                    =	50450;
	public static final int ND_PFI_28                    =	50460;
	public static final int ND_PFI_29                    =	50470;
	public static final int ND_PFI_30                    =	50480;
	public static final int ND_PFI_31                    =	50490;
	public static final int ND_PFI_32                    =	50500;
	public static final int ND_PFI_33                    =	50510;
	public static final int ND_PFI_34                    =	50520;
	public static final int ND_PFI_35                    =	50530;
	public static final int ND_PFI_36                    =	50540;
	public static final int ND_PFI_37                    =	50550;
	public static final int ND_PFI_38                    =	50560;
	public static final int ND_PFI_39                    =	50570;

	public static final int ND_PLL_REF_FREQ              =	29010;
	public static final int ND_PLL_REF_SOURCE            =	29020;
	public static final int ND_PRE_ARM                   =	29050;
	public static final int ND_POSITIVE                  =	29100;
	public static final int ND_PREPARE                   =	29200;
	public static final int ND_PROGRAM                   =	29300;
	public static final int ND_PULSE                     =	29350;
	public static final int ND_PULSE_SOURCE              =	29500;
	public static final int ND_PULSE_TRAIN_GNR           =	29600;

	public static final int ND_REGLITCH                  =	31000;
	public static final int ND_RESERVED                  =	31100;
	public static final int ND_RESET                     =	31200;
	public static final int ND_RESUME                    =	31250;
	public static final int ND_RETRIG_PULSE_GNR          =	31300;
	public static final int ND_REVISION                  =	31350;
	public static final int ND_RTSI_0                    =	31400;
	public static final int ND_RTSI_1                    =	31500;
	public static final int ND_RTSI_2                    =	31600;
	public static final int ND_RTSI_3                    =	31700;
	public static final int ND_RTSI_4                    =	31800;
	public static final int ND_RTSI_5                    =	31900;
	public static final int ND_RTSI_6                    =	32000;
	public static final int ND_RTSI_CLOCK                =	32100;

	public static final int ND_SCANCLK                   =	32400;
	public static final int ND_SCANCLK_LINE              =	32420;
	public static final int ND_SC_2040_MODE              =	32500;
	public static final int ND_SC_2043_MODE              =	32600;
	public static final int ND_SELF_CALIBRATE            =	32700;
	public static final int ND_SET_DEFAULT_LOAD_AREA     =	32800;
	public static final int ND_RESTORE_FACTORY_CALIBRATION	= 32810;
	public static final int ND_SIMPLE_EVENT_CNT          =	33100;
	public static final int ND_SINGLE                    =	33150;
	public static final int ND_SINGLE_PERIOD_MSR         =	33200;
	public static final int ND_SINGLE_PULSE_GNR          =	33300;
	public static final int ND_SINGLE_PULSE_WIDTH_MSR    =	33400;
	public static final int ND_SINGLE_TRIG_PULSE_GNR     =	33500;
	public static final int ND_SOFTWARE                  =	33600;
	public static final int ND_SOURCE                    =	33700;
	public static final int ND_SOURCE_POLARITY           =	33800;
	public static final int ND_STEPPED                   =	33825;
	public static final int ND_STRAIN_GAUGE              =	33850;
	public static final int ND_STRAIN_GAUGE_EX0          =	33875;
	public static final int ND_SUB_REVISION              =	33900;
	public static final int ND_SYNC_DUTY_CYCLE_HIGH      =	33930;
	public static final int ND_SYNC_OUT                  =	33970;

	public static final int ND_TC_REACHED                =	34100;
	public static final int ND_THE_AI_CHANNEL            =	34400;
	public static final int ND_TOGGLE                    =	34700;
	public static final int ND_TOGGLE_GATE               =	34800;
	public static final int ND_TRACK_AND_HOLD            =	34850;
	public static final int ND_TRIG_PULSE_WIDTH_MSR      =	34900;
	public static final int ND_TRIGGER_SOURCE            =	34930;
	public static final int ND_TRIGGER_MODE              =	34970;

	public static final int ND_UI2_TC                    =	35100;
	public static final int ND_UP_DOWN                   =	35150;
	public static final int ND_UP_TO_1_DMA_CHANNEL       =	35200;
	public static final int ND_UP_TO_2_DMA_CHANNELS      =	35300;
	public static final int ND_USE_CAL_CHAN              =	36000;
	public static final int ND_USE_AUX_CHAN              =	36100;
	public static final int ND_USER_EEPROM_AREA          =	37000;
	public static final int ND_USER_EEPROM_AREA_2        =	37010;
	public static final int ND_USER_EEPROM_AREA_3        =	37020;
	public static final int ND_USER_EEPROM_AREA_4        =	37030;
	public static final int ND_USER_EEPROM_AREA_5        =	37040;

	public static final int ND_DSA_RTSI_CLOCK_AD         =	44000;
	public static final int ND_DSA_RTSI_CLOCK_DA         =	44010;
	public static final int ND_DSA_OUTPUT_TRIGGER        =	44020;
	public static final int ND_DSA_INPUT_TRIGGER         =	44030;
	public static final int ND_DSA_SHARC_TRIGGER         =	44040;
	public static final int ND_DSA_ANALOG_TRIGGER        =	44050;
	public static final int ND_DSA_HOST_TRIGGER          =	44060;
	public static final int ND_DSA_EXTERNAL_DIGITAL_TRIGGER	= 44070;

	public static final int ND_VOLTAGE_OUTPUT            =	40100;
	public static final int ND_VOLTAGE_REFERENCE         =	38000;

	public static final int ND_VXI_SC                    =	((short)(0x2000));
	public static final int ND_PXI_SC                    =	((short)(0x2010));
	public static final int ND_VXIMIO_SET_ALLOCATE_MODE  =	43100;
	public static final int ND_VXIMIO_USE_ONBOARD_MEMORY_AI	= 43500;
	public static final int ND_VXIMIO_USE_ONBOARD_MEMORY_AO	= 43600;
	public static final int ND_VXIMIO_USE_ONBOARD_MEMORY_GPCTR	= 43700;
	public static final int ND_VXIMIO_USE_PC_MEMORY_AI   =	43200;
	public static final int ND_VXIMIO_USE_PC_MEMORY_AO   =	43300;
	public static final int ND_VXIMIO_USE_PC_MEMORY_GPCTR=	43400;

	public static final int ND_YES                       =	39100;
	public static final int ND_3V_LEVEL                  =	43450;

	public static final int ND_WRITE_MARK                =	50000;
	public static final int ND_READ_MARK                 =	50010;
	public static final int ND_BUFFER_START              =	50020;
	public static final int ND_BUFFER_MODE               =	50030;
	public static final int ND_DOUBLE                    =	50050;
	public static final int ND_INPUT_CONDITIONING        =	50060;
	public static final int ND_QUADRATURE_ENCODER_X1     =	50070;
	public static final int ND_QUADRATURE_ENCODER_X2     =	50080;
	public static final int ND_QUADRATURE_ENCODER_X4     =	50090;
	public static final int ND_TWO_PULSE_COUNTING        =	50100;
	public static final int ND_LINE_FILTER               =	50110;
	public static final int ND_SYNCHRONIZATION_ONLY      =	50120;
	public static final int ND_100KHZ                    =	50130;
	public static final int ND_500KHZ                    =	50140;
	public static final int ND_1MHZ                      =	50150;
	public static final int ND_5MHZ                      =	50160;

	public static final int ND_OTHER_GPCTR_SOURCE        =	50580;
	public static final int ND_OTHER_GPCTR_GATE          =	50590;
	public static final int ND_SECOND_GATE               =	50600;
	public static final int ND_SECOND_GATE_POLARITY      =	50610;
	public static final int ND_RELOAD_ON_GATE            =	50620;
	public static final int ND_TWO_SIGNAL_EDGE_SEPARATION_MSR =	50630;
	public static final int ND_BUFFERED_TWO_SIGNAL_EDGE_SEPARATION_MSR =	50640;
	public static final int ND_SWITCH_CYCLE              =	50650;
	public static final int ND_INTERNAL_MAX_TIMEBASE     =	50660;
	public static final int ND_PRESCALE_VALUE            =	50670;
	public static final int ND_ONE                       =	50680;
	public static final int ND_MAX_PRESCALE              =	50690;
	public static final int ND_Z_INDEX_PULSE             =	50700;
	public static final int ND_INTERNAL_LINE_0           =	50710;
	public static final int ND_INTERNAL_LINE_1           =	50720;
	public static final int ND_INTERNAL_LINE_2           =	50730;
	public static final int ND_INTERNAL_LINE_3           =	50740;
	public static final int ND_INTERNAL_LINE_4           =	50750;
	public static final int ND_INTERNAL_LINE_5           =	50760;
	public static final int ND_INTERNAL_LINE_6           =	50770;
	public static final int ND_INTERNAL_LINE_7           =	50780;
	public static final int ND_INTERNAL_LINE_8           =	50790;
	public static final int ND_INTERNAL_LINE_9           =	50800;
	public static final int ND_INTERNAL_LINE_10          =	50810;
	public static final int ND_INTERNAL_LINE_11          =	50820;
	public static final int ND_INTERNAL_LINE_12          =	50830;
	public static final int ND_INTERNAL_LINE_13          =	50840;
	public static final int ND_INTERNAL_LINE_14          =	50850;
	public static final int ND_INTERNAL_LINE_15          =	50860;
} // End interface
