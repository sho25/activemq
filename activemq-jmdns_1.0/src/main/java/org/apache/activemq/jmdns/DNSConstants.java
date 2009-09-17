begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2003-2005 Arthur van Hoff, Rick Blair  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jmdns
package|;
end_package

begin_comment
comment|/**  * DNS constants.  *  * @version %I%, %G%  * @author	Arthur van Hoff, Jeff Sonstein, Werner Randelshofer, Pierre Frisch, Rick Blair  */
end_comment

begin_class
specifier|final
class|class
name|DNSConstants
block|{
comment|// changed to final class - jeffs
specifier|final
specifier|static
name|String
name|MDNS_GROUP
init|=
literal|"224.0.0.251"
decl_stmt|;
specifier|final
specifier|static
name|String
name|MDNS_GROUP_IPV6
init|=
literal|"FF02::FB"
decl_stmt|;
specifier|final
specifier|static
name|int
name|MDNS_PORT
init|=
literal|5353
decl_stmt|;
specifier|final
specifier|static
name|int
name|DNS_PORT
init|=
literal|53
decl_stmt|;
specifier|final
specifier|static
name|int
name|DNS_TTL
init|=
literal|60
operator|*
literal|60
decl_stmt|;
comment|// default one hour TTL
comment|// final static int DNS_TTL		    = 120 * 60;	// two hour TTL (draft-cheshire-dnsext-multicastdns.txt ch 13)
specifier|final
specifier|static
name|int
name|MAX_MSG_TYPICAL
init|=
literal|1460
decl_stmt|;
specifier|final
specifier|static
name|int
name|MAX_MSG_ABSOLUTE
init|=
literal|8972
decl_stmt|;
specifier|final
specifier|static
name|int
name|FLAGS_QR_MASK
init|=
literal|0x8000
decl_stmt|;
comment|// Query response mask
specifier|final
specifier|static
name|int
name|FLAGS_QR_QUERY
init|=
literal|0x0000
decl_stmt|;
comment|// Query
specifier|final
specifier|static
name|int
name|FLAGS_QR_RESPONSE
init|=
literal|0x8000
decl_stmt|;
comment|// Response
specifier|final
specifier|static
name|int
name|FLAGS_AA
init|=
literal|0x0400
decl_stmt|;
comment|// Authorative answer
specifier|final
specifier|static
name|int
name|FLAGS_TC
init|=
literal|0x0200
decl_stmt|;
comment|// Truncated
specifier|final
specifier|static
name|int
name|FLAGS_RD
init|=
literal|0x0100
decl_stmt|;
comment|// Recursion desired
specifier|final
specifier|static
name|int
name|FLAGS_RA
init|=
literal|0x8000
decl_stmt|;
comment|// Recursion available
specifier|final
specifier|static
name|int
name|FLAGS_Z
init|=
literal|0x0040
decl_stmt|;
comment|// Zero
specifier|final
specifier|static
name|int
name|FLAGS_AD
init|=
literal|0x0020
decl_stmt|;
comment|// Authentic data
specifier|final
specifier|static
name|int
name|FLAGS_CD
init|=
literal|0x0010
decl_stmt|;
comment|// Checking disabled
specifier|final
specifier|static
name|int
name|CLASS_IN
init|=
literal|1
decl_stmt|;
comment|// Final Static Internet
specifier|final
specifier|static
name|int
name|CLASS_CS
init|=
literal|2
decl_stmt|;
comment|// CSNET
specifier|final
specifier|static
name|int
name|CLASS_CH
init|=
literal|3
decl_stmt|;
comment|// CHAOS
specifier|final
specifier|static
name|int
name|CLASS_HS
init|=
literal|4
decl_stmt|;
comment|// Hesiod
specifier|final
specifier|static
name|int
name|CLASS_NONE
init|=
literal|254
decl_stmt|;
comment|// Used in DNS UPDATE [RFC 2136]
specifier|final
specifier|static
name|int
name|CLASS_ANY
init|=
literal|255
decl_stmt|;
comment|// Not a DNS class, but a DNS query class, meaning "all classes"
specifier|final
specifier|static
name|int
name|CLASS_MASK
init|=
literal|0x7FFF
decl_stmt|;
comment|// Multicast DNS uses the bottom 15 bits to identify the record class...
specifier|final
specifier|static
name|int
name|CLASS_UNIQUE
init|=
literal|0x8000
decl_stmt|;
comment|// ... and the top bit indicates that all other cached records are now invalid
specifier|final
specifier|static
name|int
name|TYPE_IGNORE
init|=
literal|0
decl_stmt|;
comment|// This is a hack to stop further processing
specifier|final
specifier|static
name|int
name|TYPE_A
init|=
literal|1
decl_stmt|;
comment|// Address
specifier|final
specifier|static
name|int
name|TYPE_NS
init|=
literal|2
decl_stmt|;
comment|// Name Server
specifier|final
specifier|static
name|int
name|TYPE_MD
init|=
literal|3
decl_stmt|;
comment|// Mail Destination
specifier|final
specifier|static
name|int
name|TYPE_MF
init|=
literal|4
decl_stmt|;
comment|// Mail Forwarder
specifier|final
specifier|static
name|int
name|TYPE_CNAME
init|=
literal|5
decl_stmt|;
comment|// Canonical Name
specifier|final
specifier|static
name|int
name|TYPE_SOA
init|=
literal|6
decl_stmt|;
comment|// Start of Authority
specifier|final
specifier|static
name|int
name|TYPE_MB
init|=
literal|7
decl_stmt|;
comment|// Mailbox
specifier|final
specifier|static
name|int
name|TYPE_MG
init|=
literal|8
decl_stmt|;
comment|// Mail Group
specifier|final
specifier|static
name|int
name|TYPE_MR
init|=
literal|9
decl_stmt|;
comment|// Mail Rename
specifier|final
specifier|static
name|int
name|TYPE_NULL
init|=
literal|10
decl_stmt|;
comment|// NULL RR
specifier|final
specifier|static
name|int
name|TYPE_WKS
init|=
literal|11
decl_stmt|;
comment|// Well-known-service
specifier|final
specifier|static
name|int
name|TYPE_PTR
init|=
literal|12
decl_stmt|;
comment|// Domain Name pofinal static inter
specifier|final
specifier|static
name|int
name|TYPE_HINFO
init|=
literal|13
decl_stmt|;
comment|// Host information
specifier|final
specifier|static
name|int
name|TYPE_MINFO
init|=
literal|14
decl_stmt|;
comment|// Mailbox information
specifier|final
specifier|static
name|int
name|TYPE_MX
init|=
literal|15
decl_stmt|;
comment|// Mail exchanger
specifier|final
specifier|static
name|int
name|TYPE_TXT
init|=
literal|16
decl_stmt|;
comment|// Arbitrary text string
specifier|final
specifier|static
name|int
name|TYPE_RP
init|=
literal|17
decl_stmt|;
comment|// for Responsible Person                 [RFC1183]
specifier|final
specifier|static
name|int
name|TYPE_AFSDB
init|=
literal|18
decl_stmt|;
comment|// for AFS Data Base location             [RFC1183]
specifier|final
specifier|static
name|int
name|TYPE_X25
init|=
literal|19
decl_stmt|;
comment|// for X.25 PSDN address                  [RFC1183]
specifier|final
specifier|static
name|int
name|TYPE_ISDN
init|=
literal|20
decl_stmt|;
comment|// for ISDN address                       [RFC1183]
specifier|final
specifier|static
name|int
name|TYPE_RT
init|=
literal|21
decl_stmt|;
comment|// for Route Through                      [RFC1183]
specifier|final
specifier|static
name|int
name|TYPE_NSAP
init|=
literal|22
decl_stmt|;
comment|// for NSAP address, NSAP style A record  [RFC1706]
specifier|final
specifier|static
name|int
name|TYPE_NSAP_PTR
init|=
literal|23
decl_stmt|;
comment|//
specifier|final
specifier|static
name|int
name|TYPE_SIG
init|=
literal|24
decl_stmt|;
comment|// for security signature                 [RFC2931]
specifier|final
specifier|static
name|int
name|TYPE_KEY
init|=
literal|25
decl_stmt|;
comment|// for security key                       [RFC2535]
specifier|final
specifier|static
name|int
name|TYPE_PX
init|=
literal|26
decl_stmt|;
comment|// X.400 mail mapping information         [RFC2163]
specifier|final
specifier|static
name|int
name|TYPE_GPOS
init|=
literal|27
decl_stmt|;
comment|// Geographical Position                  [RFC1712]
specifier|final
specifier|static
name|int
name|TYPE_AAAA
init|=
literal|28
decl_stmt|;
comment|// IP6 Address                            [Thomson]
specifier|final
specifier|static
name|int
name|TYPE_LOC
init|=
literal|29
decl_stmt|;
comment|// Location Information                   [Vixie]
specifier|final
specifier|static
name|int
name|TYPE_NXT
init|=
literal|30
decl_stmt|;
comment|// Next Domain - OBSOLETE                 [RFC2535, RFC3755]
specifier|final
specifier|static
name|int
name|TYPE_EID
init|=
literal|31
decl_stmt|;
comment|// Endpoint Identifier                    [Patton]
specifier|final
specifier|static
name|int
name|TYPE_NIMLOC
init|=
literal|32
decl_stmt|;
comment|// Nimrod Locator                         [Patton]
specifier|final
specifier|static
name|int
name|TYPE_SRV
init|=
literal|33
decl_stmt|;
comment|// Server Selection                       [RFC2782]
specifier|final
specifier|static
name|int
name|TYPE_ATMA
init|=
literal|34
decl_stmt|;
comment|// ATM Address                            [Dobrowski]
specifier|final
specifier|static
name|int
name|TYPE_NAPTR
init|=
literal|35
decl_stmt|;
comment|// Naming Authority Pointer               [RFC2168, RFC2915]
specifier|final
specifier|static
name|int
name|TYPE_KX
init|=
literal|36
decl_stmt|;
comment|// Key Exchanger                          [RFC2230]
specifier|final
specifier|static
name|int
name|TYPE_CERT
init|=
literal|37
decl_stmt|;
comment|// CERT                                   [RFC2538]
specifier|final
specifier|static
name|int
name|TYPE_A6
init|=
literal|38
decl_stmt|;
comment|// A6                                     [RFC2874]
specifier|final
specifier|static
name|int
name|TYPE_DNAME
init|=
literal|39
decl_stmt|;
comment|// DNAME                                  [RFC2672]
specifier|final
specifier|static
name|int
name|TYPE_SINK
init|=
literal|40
decl_stmt|;
comment|// SINK                                   [Eastlake]
specifier|final
specifier|static
name|int
name|TYPE_OPT
init|=
literal|41
decl_stmt|;
comment|// OPT                                    [RFC2671]
specifier|final
specifier|static
name|int
name|TYPE_APL
init|=
literal|42
decl_stmt|;
comment|// APL                                    [RFC3123]
specifier|final
specifier|static
name|int
name|TYPE_DS
init|=
literal|43
decl_stmt|;
comment|// Delegation Signer                      [RFC3658]
specifier|final
specifier|static
name|int
name|TYPE_SSHFP
init|=
literal|44
decl_stmt|;
comment|// SSH Key Fingerprint                    [RFC-ietf-secsh-dns-05.txt]
specifier|final
specifier|static
name|int
name|TYPE_RRSIG
init|=
literal|46
decl_stmt|;
comment|// RRSIG                                  [RFC3755]
specifier|final
specifier|static
name|int
name|TYPE_NSEC
init|=
literal|47
decl_stmt|;
comment|// NSEC                                   [RFC3755]
specifier|final
specifier|static
name|int
name|TYPE_DNSKEY
init|=
literal|48
decl_stmt|;
comment|// DNSKEY                                 [RFC3755]
specifier|final
specifier|static
name|int
name|TYPE_UINFO
init|=
literal|100
decl_stmt|;
comment|//									      [IANA-Reserved]
specifier|final
specifier|static
name|int
name|TYPE_UID
init|=
literal|101
decl_stmt|;
comment|//                                        [IANA-Reserved]
specifier|final
specifier|static
name|int
name|TYPE_GID
init|=
literal|102
decl_stmt|;
comment|//                                        [IANA-Reserved]
specifier|final
specifier|static
name|int
name|TYPE_UNSPEC
init|=
literal|103
decl_stmt|;
comment|//                                        [IANA-Reserved]
specifier|final
specifier|static
name|int
name|TYPE_TKEY
init|=
literal|249
decl_stmt|;
comment|// Transaction Key                        [RFC2930]
specifier|final
specifier|static
name|int
name|TYPE_TSIG
init|=
literal|250
decl_stmt|;
comment|// Transaction Signature                  [RFC2845]
specifier|final
specifier|static
name|int
name|TYPE_IXFR
init|=
literal|251
decl_stmt|;
comment|// Incremental transfer                   [RFC1995]
specifier|final
specifier|static
name|int
name|TYPE_AXFR
init|=
literal|252
decl_stmt|;
comment|// Transfer of an entire zone             [RFC1035]
specifier|final
specifier|static
name|int
name|TYPE_MAILA
init|=
literal|253
decl_stmt|;
comment|// Mailbox-related records (MB, MG or MR) [RFC1035]
specifier|final
specifier|static
name|int
name|TYPE_MAILB
init|=
literal|254
decl_stmt|;
comment|// Mail agent RRs (Obsolete - see MX)     [RFC1035]
specifier|final
specifier|static
name|int
name|TYPE_ANY
init|=
literal|255
decl_stmt|;
comment|// Request for all records	        	  [RFC1035]
comment|//Time Intervals for various functions
specifier|final
specifier|static
name|int
name|SHARED_QUERY_TIME
init|=
literal|20
decl_stmt|;
comment|//milliseconds before send shared query
specifier|final
specifier|static
name|int
name|QUERY_WAIT_INTERVAL
init|=
literal|225
decl_stmt|;
comment|//milliseconds between query loops.
specifier|final
specifier|static
name|int
name|PROBE_WAIT_INTERVAL
init|=
literal|250
decl_stmt|;
comment|//milliseconds between probe loops.
specifier|final
specifier|static
name|int
name|RESPONSE_MIN_WAIT_INTERVAL
init|=
literal|20
decl_stmt|;
comment|//minimal wait interval for response.
specifier|final
specifier|static
name|int
name|RESPONSE_MAX_WAIT_INTERVAL
init|=
literal|115
decl_stmt|;
comment|//maximal wait interval for response
specifier|final
specifier|static
name|int
name|PROBE_CONFLICT_INTERVAL
init|=
literal|1000
decl_stmt|;
comment|//milliseconds to wait after conflict.
specifier|final
specifier|static
name|int
name|PROBE_THROTTLE_COUNT
init|=
literal|10
decl_stmt|;
comment|//After x tries go 1 time a sec. on probes.
specifier|final
specifier|static
name|int
name|PROBE_THROTTLE_COUNT_INTERVAL
init|=
literal|5000
decl_stmt|;
comment|//We only increment the throttle count, if
comment|// the previous increment is inside this interval.
specifier|final
specifier|static
name|int
name|ANNOUNCE_WAIT_INTERVAL
init|=
literal|1000
decl_stmt|;
comment|//milliseconds between Announce loops.
specifier|final
specifier|static
name|int
name|RECORD_REAPER_INTERVAL
init|=
literal|10000
decl_stmt|;
comment|//milliseconds between cache cleanups.
specifier|final
specifier|static
name|int
name|KNOWN_ANSWER_TTL
init|=
literal|120
decl_stmt|;
specifier|final
specifier|static
name|int
name|ANNOUNCED_RENEWAL_TTL_INTERVAL
init|=
name|DNS_TTL
operator|*
literal|500
decl_stmt|;
comment|// 50% of the TTL in milliseconds
block|}
end_class

end_unit

