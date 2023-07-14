/*********************************************************************/
/*                                                                   */
/* This file contains definitions for constants required for some    */
/* of the NI-DAQ functions.                                          */
/*                                                                   */
/* You should use symbols defined here in your programs;  do not     */
/* use the numerical values.                                         */
/*                                                                   */
/* See your NI-DAQ Function Reference Manual for details concerning  */
/* use of constants defined here.                                    */
/*                                                                   */
/*********************************************************************/
#if !defined (___nidaqcns_h___)
#define ___nidaqcns_h___
#define ND_ABOVE_HIGH_LEVEL           	11020L
#define ND_AC                         	11025L
#define ND_ACK_REQ_EXCHANGE_GR1       	11030L
#define ND_ACK_REQ_EXCHANGE_GR2       	11035L
#define ND_ACTIVE                     	11037L
#define ND_ADC_RESOLUTION             	11040L
#define ND_AI_CALDAC_COUNT            	11050L
#define ND_AI_CHANNEL_COUNT           	11060L
#define ND_AI_COUPLING                	11055L
#define ND_AI_FIFO_INTERRUPTS         	11600L
#define ND_ANALOG_FILTER              	11065L
#define ND_AO48XDC_SET_POWERUP_STATE  	42100L
#define ND_AO_CALDAC_COUNT            	11070L
#define ND_AO_CHANNEL_COUNT           	11080L
#define ND_AO_EXT_REF_CAPABLE         	11090L
#define ND_AO_UNIPOLAR_CAPABLE        	11095L
#define ND_ARM                        	11100L
#define ND_ARMED                      	11200L
#define ND_ATC_OUT                    	11250L
#define ND_ATTENUATION                	11260L
#define ND_AUTOINCREMENT_COUNT        	11300L
#define ND_AUTOMATIC                  	11400L
#define ND_AVAILABLE_POINTS           	11500L

#define ND_BASE_ADDRESS               	12100L
#define ND_BELOW_LOW_LEVEL            	12130L
#define ND_BOARD_CLOCK                	12170L
#define ND_BUFFERED_EVENT_CNT         	12200L
#define ND_BUFFERED_PERIOD_MSR        	12300L
#define ND_BUFFERED_PULSE_WIDTH_MSR   	12400L
#define ND_BUFFERED_SEMI_PERIOD_MSR   	12500L
#define ND_BURST                      	12600L
#define ND_BURST_INTERVAL             	12700L

#define ND_CAL_CONST_AUTO_LOAD        	13050L
#define ND_CALIBRATION_FRAME_SIZE     	13060L
#define ND_CALIBRATION_FRAME_PTR      	13065L
#define ND_CJ_TEMP                    	((short)(0x8000))
#define ND_CALGND                     	((short)(0x8001))
#define ND_CLEAN_UP                   	13100L
#define ND_CLOCK_REVERSE_MODE_GR1     	13120L
#define ND_CLOCK_REVERSE_MODE_GR2     	13130L
#define ND_CONFIG_MEMORY_SIZE         	13150L
#define ND_CONTINUOUS                 	13160L
#define ND_COUNT                      	13200L

#define ND_COUNTER_0                  	13300L
#define ND_COUNTER_1                  	13400L
#define ND_COUNTER_2                  	13310L
#define ND_COUNTER_3                  	13320L
#define ND_COUNTER_4                  	13330L
#define ND_COUNTER_5                  	13340L
#define ND_COUNTER_6                  	13350L
#define ND_COUNTER_7                  	13360L

#define ND_COUNTER_1_SOURCE           	13430L
#define ND_COUNT_AVAILABLE            	13450L
#define ND_COUNT_DOWN                 	13465L
#define ND_COUNT_UP                   	13485L
#define ND_COUNT_1                    	13500L
#define ND_COUNT_2                    	13600L
#define ND_COUNT_3                    	13700L
#define ND_COUNT_4                    	13800L
#define ND_CURRENT_OUTPUT             	40200L

#define ND_DAC_RESOLUTION             	13950L
#define ND_DATA_TRANSFER_CONDITION    	13960L
#define ND_DATA_XFER_MODE_AI          	14000L
#define ND_DATA_XFER_MODE_AO_GR1      	14100L
#define ND_DATA_XFER_MODE_AO_GR2      	14200L
#define ND_DATA_XFER_MODE_DIO_GR1     	14300L
#define ND_DATA_XFER_MODE_DIO_GR2     	14400L
#define ND_DATA_XFER_MODE_DIO_GR3     	14500L
#define ND_DATA_XFER_MODE_DIO_GR4     	14600L
#define ND_DATA_XFER_MODE_DIO_GR5     	14700L
#define ND_DATA_XFER_MODE_DIO_GR6     	14800L
#define ND_DATA_XFER_MODE_DIO_GR7     	14900L
#define ND_DATA_XFER_MODE_DIO_GR8     	15000L

#define ND_DATA_XFER_MODE_GPCTR0      	15100L
#define ND_DATA_XFER_MODE_GPCTR1      	15200L
#define ND_DATA_XFER_MODE_GPCTR2      	15110L
#define ND_DATA_XFER_MODE_GPCTR3      	15120L
#define ND_DATA_XFER_MODE_GPCTR4      	15130L
#define ND_DATA_XFER_MODE_GPCTR5      	15140L
#define ND_DATA_XFER_MODE_GPCTR6      	15150L
#define ND_DATA_XFER_MODE_GPCTR7      	15160L

#define ND_DC                         	15250L
#define ND_DEVICE_NAME                	15260L
#define ND_DEVICE_POWER               	15270L
#define ND_DEVICE_SERIAL_NUMBER       	15280L
#define ND_DEVICE_STATE_DURING_SUSPEND_MODE	15290L
#define ND_DEVICE_TYPE_CODE           	15300L
#define ND_DIGITAL_FILTER             	15350L
#define ND_DIGITAL_RESTART            	15375L
#define ND_DIO128_GET_PORT_THRESHOLD  	41200L
#define ND_DIO128_SELECT_INPUT_PORT   	41100L
#define ND_DIO128_SET_PORT_THRESHOLD  	41300L
#define ND_DISABLED                   	15400L
#define ND_DISARM                     	15450L
#define ND_DMA_A_LEVEL                	15500L
#define ND_DMA_B_LEVEL                	15600L
#define ND_DMA_C_LEVEL                	15700L
#define ND_DONE                       	15800L
#define ND_DONT_CARE                  	15900L
#define ND_DONT_KNOW                  	15950L

#define ND_EDGE_SENSITIVE             	16000L
#define ND_ENABLED                    	16050L
#define ND_END                        	16055L
#define ND_EXTERNAL                   	16060L
#define ND_EXTERNAL_CALIBRATE         	16100L

#define ND_FACTORY_CALIBRATION_DATE   	16200L
#define ND_FACTORY_CALIBRATION_EQUIP  	16210L
#define ND_FACTORY_EEPROM_AREA        	16220L
#define ND_FIFO_EMPTY                 	16230L
#define ND_FIFO_HALF_FULL_OR_LESS     	16240L
#define ND_FIFO_HALF_FULL_OR_LESS_UNTIL_FULL	16245L
#define ND_FIFO_NOT_FULL              	16250L
#define ND_FIFO_TRANSFER_COUNT        	16260L
#define ND_FILTER_CORRECTION_FREQ     	16300L
#define ND_FOREGROUND                 	16350L
#define ND_FREQ_OUT                   	16400L
#define ND_FSK                        	16500L

#define ND_GATE                       	17100L
#define ND_GATE_POLARITY              	17200L

#define ND_GPCTR0_GATE                	17300L
#define ND_GPCTR0_OUTPUT              	17400L
#define ND_GPCTR0_SOURCE              	17500L

#define ND_GPCTR1_GATE                	17600L
#define ND_GPCTR1_OUTPUT              	17700L
#define ND_GPCTR1_SOURCE              	17800L

#define ND_GPCTR2_GATE                	17320L
#define ND_GPCTR2_OUTPUT              	17420L
#define ND_GPCTR2_SOURCE              	17520L

#define ND_GPCTR3_GATE                	17330L
#define ND_GPCTR3_OUTPUT              	17430L
#define ND_GPCTR3_SOURCE              	17530L

#define ND_GPCTR4_GATE                	17340L
#define ND_GPCTR4_OUTPUT              	17440L
#define ND_GPCTR4_SOURCE              	17540L

#define ND_GPCTR5_GATE                	17350L
#define ND_GPCTR5_OUTPUT              	17450L
#define ND_GPCTR5_SOURCE              	17550L

#define ND_GPCTR6_GATE                	17360L
#define ND_GPCTR6_OUTPUT              	17460L
#define ND_GPCTR6_SOURCE              	17660L

#define ND_GPCTR7_GATE                	17370L
#define ND_GPCTR7_OUTPUT              	17470L
#define ND_GPCTR7_SOURCE              	17570L

#define ND_GROUND_DAC_REFERENCE       	17900L

#define ND_HARDWARE                   	18000L
#define ND_HIGH                       	18050L
#define ND_HIGH_HYSTERESIS            	18080L
#define ND_HIGH_TO_LOW                	18100L
#define ND_HW_ANALOG_TRIGGER          	18900L

#define ND_IMPEDANCE                  	19000L
#define ND_INACTIVE                   	19010L
#define ND_INITIAL_COUNT              	19100L
#define ND_INIT_PLUGPLAY_DEVICES      	19110L
#define ND_INSIDE_REGION              	19150L
#define ND_INTERNAL                   	19160L
#define ND_INTERNAL_100_KHZ           	19200L
#define ND_INTERNAL_10_MHZ            	19300L
#define ND_INTERNAL_1250_KHZ          	19320L
#define ND_INTERNAL_20_MHZ            	19400L
#define ND_INTERNAL_2500_KHZ          	19420L
#define ND_INTERNAL_7160_KHZ          	19460L
#define ND_INTERNAL_TIMER             	19500L
#define ND_INTERRUPTS                 	19600L
#define ND_INTERRUPT_A_LEVEL          	19700L
#define ND_INTERRUPT_B_LEVEL          	19800L
#define ND_INTERRUPT_TRIGGER_MODE     	19850L
#define ND_IN_CHANNEL_CLOCK_TIMEBASE  	19900L
#define ND_IN_CHANNEL_CLOCK_TB_POL    	20000L
#define ND_IN_CONVERT                 	20100L
#define ND_IN_CONVERT_POL             	20200L
#define ND_IN_DATA_FIFO_SIZE          	20250L
#define ND_IN_EXTERNAL_GATE           	20300L
#define ND_IN_EXTERNAL_GATE_POL       	20400L
#define ND_IN_SCAN_CLOCK_TIMEBASE     	20500L
#define ND_IN_SCAN_CLOCK_TB_POL       	20600L
#define ND_IN_SCAN_IN_PROG            	20650L
#define ND_IN_SCAN_START              	20700L
#define ND_IN_SCAN_START_POL          	20800L
#define ND_IN_START_TRIGGER           	20900L
#define ND_IN_START_TRIGGER_POL       	21000L
#define ND_IN_STOP_TRIGGER            	21100L
#define ND_IN_STOP_TRIGGER_POL        	21200L
#define ND_INT_AI_GND                 	21210L
#define ND_INT_AO_CH_0                	21230L
#define ND_INT_AO_CH_0_VS_REF_5V      	21235L
#define ND_INT_AO_CH_1                	21240L
#define ND_INT_AO_CH_1_VS_AO_CH_0     	21245L
#define ND_INT_AO_CH_1_VS_REF_5V      	21250L
#define ND_INT_AO_GND                 	21260L
#define ND_INT_AO_GND_VS_AI_GND       	21265L
#define ND_INT_CM_REF_5V              	21270L
#define ND_INT_DEV_TEMP               	21280L
#define ND_INT_REF_5V                 	21290L
#define ND_INT_CAL_BUS                	21295L
#define ND_INT_MUX_BUS                	21305L

#define ND_INT_AI_GND_AMP_0           	21211L
#define ND_INT_AI_GND_AMP_1           	21212L
#define ND_INT_AI_GND_AMP_2           	21213L
#define ND_INT_AI_GND_AMP_3           	21214L
#define ND_INT_AO_CH_0_AMP_0          	21231L
#define ND_INT_AO_CH_0_AMP_1          	21232L
#define ND_INT_AO_CH_0_AMP_2          	21233L
#define ND_INT_AO_CH_0_AMP_3          	21234L
#define ND_INT_AO_CH_1_AMP_0          	21241L
#define ND_INT_AO_CH_1_AMP_1          	21242L
#define ND_INT_AO_CH_1_AMP_2          	21243L
#define ND_INT_AO_CH_1_AMP_3          	21244L
#define ND_INT_AO_CH_0_VS_REF_AMP_0   	21236L
#define ND_INT_AO_CH_0_VS_REF_AMP_1   	21237L
#define ND_INT_AO_CH_0_VS_REF_AMP_2   	21238L
#define ND_INT_AO_CH_0_VS_REF_AMP_3   	21239L
#define ND_INT_AO_CH_1_VS_REF_AMP_0   	21251L
#define ND_INT_AO_CH_1_VS_REF_AMP_1   	21252L
#define ND_INT_AO_CH_1_VS_REF_AMP_2   	21253L
#define ND_INT_AO_CH_1_VS_REF_AMP_3   	21254L
#define ND_INT_AO_GND_VS_AI_GND_AMP_0 	21266L
#define ND_INT_AO_GND_VS_AI_GND_AMP_1 	21267L
#define ND_INT_AO_GND_VS_AI_GND_AMP_2 	21268L
#define ND_INT_AO_GND_VS_AI_GND_AMP_3 	21269L
#define ND_INT_CM_REF_AMP_0           	21271L
#define ND_INT_CM_REF_AMP_1           	21272L
#define ND_INT_CM_REF_AMP_2           	21273L
#define ND_INT_CM_REF_AMP_3           	21274L
#define ND_INT_REF_AMP_0              	21291L
#define ND_INT_REF_AMP_1              	21292L
#define ND_INT_REF_AMP_2              	21293L
#define ND_INT_REF_AMP_3              	21294L

#define ND_INTERRUPT_EVERY_SAMPLE     	11700L
#define ND_INTERRUPT_HALF_FIFO        	11800L
#define ND_IO_CONNECTOR               	21300L

#define ND_LEVEL_SENSITIVE            	24000L
#define ND_LINK_COMPLETE_INTERRUPTS   	24010L
#define ND_LOW                        	24050L
#define ND_LOW_HYSTERESIS             	24080L
#define ND_LOW_TO_HIGH                	24100L
#define ND_LPT_DEVICE_MODE            	24200L

#define ND_MARKER                     	24500L
#define ND_MEMORY_TRANSFER_WIDTH      	24600L

#define ND_NEGATIVE                   	26100L
#define ND_NEW                        	26190L
#define ND_NI_DAQ_SW_AREA             	26195L
#define ND_NO                         	26200L
#define ND_NO_STRAIN_GAUGE            	26225L
#define ND_NO_TRACK_AND_HOLD          	26250L
#define ND_NONE                       	26300L
#define ND_NOT_APPLICABLE             	26400L
#define ND_NUMBER_DIG_PORTS           	26500L

#define ND_OFF                        	27010L
#define ND_OFFSET                     	27020L
#define ND_ON                         	27050L
#define ND_OTHER                      	27060L
#define ND_OTHER_GPCTR_OUTPUT         	27300L
#define ND_OTHER_GPCTR_TC             	27400L
#define ND_OUT_DATA_FIFO_SIZE         	27070L
#define ND_OUT_EXTERNAL_GATE          	27080L
#define ND_OUT_EXTERNAL_GATE_POL      	27082L
#define ND_OUT_START_TRIGGER          	27100L
#define ND_OUT_START_TRIGGER_POL      	27102L
#define ND_OUT_UPDATE                 	27200L
#define ND_OUT_UPDATE_POL             	27202L
#define ND_OUT_UPDATE_CLOCK_TIMEBASE  	27210L
#define ND_OUT_UPDATE_CLOCK_TB_POL    	27212L
#define ND_OUTPUT_ENABLE              	27220L
#define ND_OUTPUT_MODE                	27230L
#define ND_OUTPUT_POLARITY            	27240L
#define ND_OUTPUT_STATE               	27250L
#define ND_OUTPUT_TYPE                	40000L

#define ND_DIGITAL_PATTERN_GENERATION 	28030L
#define ND_PAUSE                      	28040L
#define ND_PAUSE_ON_HIGH              	28045L
#define ND_PAUSE_ON_LOW               	28050L
#define ND_PFI_0                      	28100L
#define ND_PFI_1                      	28200L
#define ND_PFI_2                      	28300L
#define ND_PFI_3                      	28400L
#define ND_PFI_4                      	28500L
#define ND_PFI_5                      	28600L
#define ND_PFI_6                      	28700L
#define ND_PFI_7                      	28800L
#define ND_PFI_8                      	28900L
#define ND_PFI_9                      	29000L
#define ND_PFI_10                     	50280L
#define ND_PFI_11                     	50290L
#define ND_PFI_12                     	50300L
#define ND_PFI_13                     	50310L
#define ND_PFI_14                     	50320L
#define ND_PFI_15                     	50330L
#define ND_PFI_16                     	50340L
#define ND_PFI_17                     	50350L
#define ND_PFI_18                     	50360L
#define ND_PFI_19                     	50370L
#define ND_PFI_20                     	50380L
#define ND_PFI_21                     	50390L
#define ND_PFI_22                     	50400L
#define ND_PFI_23                     	50410L
#define ND_PFI_24                     	50420L
#define ND_PFI_25                     	50430L
#define ND_PFI_26                     	50440L
#define ND_PFI_27                     	50450L
#define ND_PFI_28                     	50460L
#define ND_PFI_29                     	50470L
#define ND_PFI_30                     	50480L
#define ND_PFI_31                     	50490L
#define ND_PFI_32                     	50500L
#define ND_PFI_33                     	50510L
#define ND_PFI_34                     	50520L
#define ND_PFI_35                     	50530L
#define ND_PFI_36                     	50540L
#define ND_PFI_37                     	50550L
#define ND_PFI_38                     	50560L
#define ND_PFI_39                     	50570L

#define ND_PLL_REF_FREQ               	29010L
#define ND_PLL_REF_SOURCE             	29020L
#define ND_PRE_ARM                    	29050L
#define ND_POSITIVE                   	29100L
#define ND_PREPARE                    	29200L
#define ND_PROGRAM                    	29300L
#define ND_PULSE                      	29350L
#define ND_PULSE_SOURCE               	29500L
#define ND_PULSE_TRAIN_GNR            	29600L

#define ND_REGLITCH                   	31000L
#define ND_RESERVED                   	31100L
#define ND_RESET                      	31200L
#define ND_RESUME                     	31250L
#define ND_RETRIG_PULSE_GNR           	31300L
#define ND_REVISION                   	31350L
#define ND_RTSI_0                     	31400L
#define ND_RTSI_1                     	31500L
#define ND_RTSI_2                     	31600L
#define ND_RTSI_3                     	31700L
#define ND_RTSI_4                     	31800L
#define ND_RTSI_5                     	31900L
#define ND_RTSI_6                     	32000L
#define ND_RTSI_CLOCK                 	32100L

#define ND_SCANCLK                    	32400L
#define ND_SCANCLK_LINE               	32420L
#define ND_SC_2040_MODE               	32500L
#define ND_SC_2043_MODE               	32600L
#define ND_SELF_CALIBRATE             	32700L
#define ND_SET_DEFAULT_LOAD_AREA      	32800L
#define ND_RESTORE_FACTORY_CALIBRATION	32810L
#define ND_SIMPLE_EVENT_CNT           	33100L
#define ND_SINGLE                     	33150L
#define ND_SINGLE_PERIOD_MSR          	33200L
#define ND_SINGLE_PULSE_GNR           	33300L
#define ND_SINGLE_PULSE_WIDTH_MSR     	33400L
#define ND_SINGLE_TRIG_PULSE_GNR      	33500L
#define ND_SOFTWARE                   	33600L
#define ND_SOURCE                     	33700L
#define ND_SOURCE_POLARITY            	33800L
#define ND_STEPPED                    	33825L
#define ND_STRAIN_GAUGE               	33850L
#define ND_STRAIN_GAUGE_EX0           	33875L
#define ND_SUB_REVISION               	33900L
#define ND_SYNC_DUTY_CYCLE_HIGH       	33930L
#define ND_SYNC_OUT                   	33970L

#define ND_TC_REACHED                 	34100L
#define ND_THE_AI_CHANNEL             	34400L
#define ND_TOGGLE                     	34700L
#define ND_TOGGLE_GATE                	34800L
#define ND_TRACK_AND_HOLD             	34850L
#define ND_TRIG_PULSE_WIDTH_MSR       	34900L
#define ND_TRIGGER_SOURCE             	34930L
#define ND_TRIGGER_MODE               	34970L

#define ND_UI2_TC                     	35100L
#define ND_UP_DOWN                    	35150L
#define ND_UP_TO_1_DMA_CHANNEL        	35200L
#define ND_UP_TO_2_DMA_CHANNELS       	35300L
#define ND_USE_CAL_CHAN               	36000L
#define ND_USE_AUX_CHAN               	36100L
#define ND_USER_EEPROM_AREA           	37000L
#define ND_USER_EEPROM_AREA_2         	37010L
#define ND_USER_EEPROM_AREA_3         	37020L
#define ND_USER_EEPROM_AREA_4         	37030L
#define ND_USER_EEPROM_AREA_5         	37040L

#define ND_DSA_RTSI_CLOCK_AD          	44000L
#define ND_DSA_RTSI_CLOCK_DA          	44010L
#define ND_DSA_OUTPUT_TRIGGER         	44020L
#define ND_DSA_INPUT_TRIGGER          	44030L
#define ND_DSA_SHARC_TRIGGER          	44040L
#define ND_DSA_ANALOG_TRIGGER         	44050L
#define ND_DSA_HOST_TRIGGER           	44060L
#define ND_DSA_EXTERNAL_DIGITAL_TRIGGER	44070L

#define ND_VOLTAGE_OUTPUT             	40100L
#define ND_VOLTAGE_REFERENCE          	38000L

#define ND_VXI_SC                     	((short)(0x2000))
#define ND_PXI_SC                     	((short)(0x2010))
#define ND_VXIMIO_SET_ALLOCATE_MODE   	43100L
#define ND_VXIMIO_USE_ONBOARD_MEMORY_AI	43500L
#define ND_VXIMIO_USE_ONBOARD_MEMORY_AO	43600L
#define ND_VXIMIO_USE_ONBOARD_MEMORY_GPCTR	43700L
#define ND_VXIMIO_USE_PC_MEMORY_AI    	43200L
#define ND_VXIMIO_USE_PC_MEMORY_AO    	43300L
#define ND_VXIMIO_USE_PC_MEMORY_GPCTR 	43400L

#define ND_YES                        	39100L
#define ND_3V_LEVEL                   	43450L

#define ND_WRITE_MARK                 	50000L
#define ND_READ_MARK                  	50010L
#define ND_BUFFER_START               	50020L
#define ND_BUFFER_MODE                	50030L
#define ND_DOUBLE                     	50050L
#define ND_INPUT_CONDITIONING         	50060L
#define ND_QUADRATURE_ENCODER_X1      	50070L
#define ND_QUADRATURE_ENCODER_X2      	50080L
#define ND_QUADRATURE_ENCODER_X4      	50090L
#define ND_TWO_PULSE_COUNTING         	50100L
#define ND_LINE_FILTER                	50110L
#define ND_SYNCHRONIZATION_ONLY       	50120L
#define ND_100KHZ                     	50130L
#define ND_500KHZ                     	50140L
#define ND_1MHZ                       	50150L
#define ND_5MHZ                       	50160L


#define ND_OTHER_GPCTR_SOURCE         	50580L
#define ND_OTHER_GPCTR_GATE           	50590L
#define ND_SECOND_GATE                	50600L
#define ND_SECOND_GATE_POLARITY       	50610L
#define ND_RELOAD_ON_GATE             	50620L
#define ND_TWO_SIGNAL_EDGE_SEPARATION_MSR	50630L
#define ND_BUFFERED_TWO_SIGNAL_EDGE_SEPARATION_MSR	50640L
#define ND_SWITCH_CYCLE               	50650L
#define ND_INTERNAL_MAX_TIMEBASE      	50660L
#define ND_PRESCALE_VALUE             	50670L
#define ND_ONE                        	50680L
#define ND_MAX_PRESCALE               	50690L
#define ND_Z_INDEX_PULSE              	50700L
#define ND_INTERNAL_LINE_0            	50710L
#define ND_INTERNAL_LINE_1            	50720L
#define ND_INTERNAL_LINE_2            	50730L
#define ND_INTERNAL_LINE_3            	50740L
#define ND_INTERNAL_LINE_4            	50750L
#define ND_INTERNAL_LINE_5            	50760L
#define ND_INTERNAL_LINE_6            	50770L
#define ND_INTERNAL_LINE_7            	50780L
#define ND_INTERNAL_LINE_8            	50790L
#define ND_INTERNAL_LINE_9            	50800L
#define ND_INTERNAL_LINE_10           	50810L
#define ND_INTERNAL_LINE_11           	50820L
#define ND_INTERNAL_LINE_12           	50830L
#define ND_INTERNAL_LINE_13           	50840L
#define ND_INTERNAL_LINE_14           	50850L
#define ND_INTERNAL_LINE_15           	50860L






#endif

