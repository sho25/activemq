begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_comment
comment|/**  * Holds the command id constants used by the command objects.  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|CommandTypes
block|{
comment|// What is the latest version of the openwire protocol
name|byte
name|PROTOCOL_VERSION
init|=
literal|7
decl_stmt|;
comment|// A marshaling layer can use this type to specify a null object.
name|byte
name|NULL
init|=
literal|0
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Info objects sent back and forth client/server when
comment|// setting up a client connection.
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|WIREFORMAT_INFO
init|=
literal|1
decl_stmt|;
name|byte
name|BROKER_INFO
init|=
literal|2
decl_stmt|;
name|byte
name|CONNECTION_INFO
init|=
literal|3
decl_stmt|;
name|byte
name|SESSION_INFO
init|=
literal|4
decl_stmt|;
name|byte
name|CONSUMER_INFO
init|=
literal|5
decl_stmt|;
name|byte
name|PRODUCER_INFO
init|=
literal|6
decl_stmt|;
name|byte
name|TRANSACTION_INFO
init|=
literal|7
decl_stmt|;
name|byte
name|DESTINATION_INFO
init|=
literal|8
decl_stmt|;
name|byte
name|REMOVE_SUBSCRIPTION_INFO
init|=
literal|9
decl_stmt|;
name|byte
name|KEEP_ALIVE_INFO
init|=
literal|10
decl_stmt|;
name|byte
name|SHUTDOWN_INFO
init|=
literal|11
decl_stmt|;
name|byte
name|REMOVE_INFO
init|=
literal|12
decl_stmt|;
name|byte
name|CONTROL_COMMAND
init|=
literal|14
decl_stmt|;
name|byte
name|FLUSH_COMMAND
init|=
literal|15
decl_stmt|;
name|byte
name|CONNECTION_ERROR
init|=
literal|16
decl_stmt|;
name|byte
name|CONSUMER_CONTROL
init|=
literal|17
decl_stmt|;
name|byte
name|CONNECTION_CONTROL
init|=
literal|18
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Messages that go back and forth between the client
comment|// and the server.
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|PRODUCER_ACK
init|=
literal|19
decl_stmt|;
name|byte
name|MESSAGE_PULL
init|=
literal|20
decl_stmt|;
name|byte
name|MESSAGE_DISPATCH
init|=
literal|21
decl_stmt|;
name|byte
name|MESSAGE_ACK
init|=
literal|22
decl_stmt|;
name|byte
name|ACTIVEMQ_MESSAGE
init|=
literal|23
decl_stmt|;
name|byte
name|ACTIVEMQ_BYTES_MESSAGE
init|=
literal|24
decl_stmt|;
name|byte
name|ACTIVEMQ_MAP_MESSAGE
init|=
literal|25
decl_stmt|;
name|byte
name|ACTIVEMQ_OBJECT_MESSAGE
init|=
literal|26
decl_stmt|;
name|byte
name|ACTIVEMQ_STREAM_MESSAGE
init|=
literal|27
decl_stmt|;
name|byte
name|ACTIVEMQ_TEXT_MESSAGE
init|=
literal|28
decl_stmt|;
name|byte
name|ACTIVEMQ_BLOB_MESSAGE
init|=
literal|29
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Command Response messages
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|RESPONSE
init|=
literal|30
decl_stmt|;
name|byte
name|EXCEPTION_RESPONSE
init|=
literal|31
decl_stmt|;
name|byte
name|DATA_RESPONSE
init|=
literal|32
decl_stmt|;
name|byte
name|DATA_ARRAY_RESPONSE
init|=
literal|33
decl_stmt|;
name|byte
name|INTEGER_RESPONSE
init|=
literal|34
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Used by discovery
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|DISCOVERY_EVENT
init|=
literal|40
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Command object used by the Journal
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|JOURNAL_ACK
init|=
literal|50
decl_stmt|;
name|byte
name|JOURNAL_REMOVE
init|=
literal|52
decl_stmt|;
name|byte
name|JOURNAL_TRACE
init|=
literal|53
decl_stmt|;
name|byte
name|JOURNAL_TRANSACTION
init|=
literal|54
decl_stmt|;
name|byte
name|DURABLE_SUBSCRIPTION_INFO
init|=
literal|55
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Reliability and fragmentation
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|PARTIAL_COMMAND
init|=
literal|60
decl_stmt|;
name|byte
name|PARTIAL_LAST_COMMAND
init|=
literal|61
decl_stmt|;
name|byte
name|REPLAY
init|=
literal|65
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Types used represent basic Java types.
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|BYTE_TYPE
init|=
literal|70
decl_stmt|;
name|byte
name|CHAR_TYPE
init|=
literal|71
decl_stmt|;
name|byte
name|SHORT_TYPE
init|=
literal|72
decl_stmt|;
name|byte
name|INTEGER_TYPE
init|=
literal|73
decl_stmt|;
name|byte
name|LONG_TYPE
init|=
literal|74
decl_stmt|;
name|byte
name|DOUBLE_TYPE
init|=
literal|75
decl_stmt|;
name|byte
name|FLOAT_TYPE
init|=
literal|76
decl_stmt|;
name|byte
name|STRING_TYPE
init|=
literal|77
decl_stmt|;
name|byte
name|BOOLEAN_TYPE
init|=
literal|78
decl_stmt|;
name|byte
name|BYTE_ARRAY_TYPE
init|=
literal|79
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Broker to Broker command objects
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|MESSAGE_DISPATCH_NOTIFICATION
init|=
literal|90
decl_stmt|;
name|byte
name|NETWORK_BRIDGE_FILTER
init|=
literal|91
decl_stmt|;
comment|// /////////////////////////////////////////////////
comment|//
comment|// Data structures contained in the command objects.
comment|//
comment|// /////////////////////////////////////////////////
name|byte
name|ACTIVEMQ_QUEUE
init|=
literal|100
decl_stmt|;
name|byte
name|ACTIVEMQ_TOPIC
init|=
literal|101
decl_stmt|;
name|byte
name|ACTIVEMQ_TEMP_QUEUE
init|=
literal|102
decl_stmt|;
name|byte
name|ACTIVEMQ_TEMP_TOPIC
init|=
literal|103
decl_stmt|;
name|byte
name|MESSAGE_ID
init|=
literal|110
decl_stmt|;
name|byte
name|ACTIVEMQ_LOCAL_TRANSACTION_ID
init|=
literal|111
decl_stmt|;
name|byte
name|ACTIVEMQ_XA_TRANSACTION_ID
init|=
literal|112
decl_stmt|;
name|byte
name|CONNECTION_ID
init|=
literal|120
decl_stmt|;
name|byte
name|SESSION_ID
init|=
literal|121
decl_stmt|;
name|byte
name|CONSUMER_ID
init|=
literal|122
decl_stmt|;
name|byte
name|PRODUCER_ID
init|=
literal|123
decl_stmt|;
name|byte
name|BROKER_ID
init|=
literal|124
decl_stmt|;
block|}
end_interface

end_unit

