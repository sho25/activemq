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
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_interface
specifier|public
interface|interface
name|Stomp
block|{
name|String
name|NULL
init|=
literal|"\u0000"
decl_stmt|;
name|String
name|NEWLINE
init|=
literal|"\n"
decl_stmt|;
name|byte
name|BREAK
init|=
literal|'\n'
decl_stmt|;
name|byte
name|COLON
init|=
literal|':'
decl_stmt|;
name|byte
name|ESCAPE
init|=
literal|'\\'
decl_stmt|;
name|byte
index|[]
name|ESCAPE_ESCAPE_SEQ
init|=
block|{
literal|92
block|,
literal|92
block|}
decl_stmt|;
name|byte
index|[]
name|COLON_ESCAPE_SEQ
init|=
block|{
literal|92
block|,
literal|99
block|}
decl_stmt|;
name|byte
index|[]
name|NEWLINE_ESCAPE_SEQ
init|=
block|{
literal|92
block|,
literal|110
block|}
decl_stmt|;
name|String
name|COMMA
init|=
literal|","
decl_stmt|;
name|String
name|V1_0
init|=
literal|"1.0"
decl_stmt|;
name|String
name|V1_1
init|=
literal|"1.1"
decl_stmt|;
name|String
name|V1_2
init|=
literal|"1.2"
decl_stmt|;
name|String
name|DEFAULT_HEART_BEAT
init|=
literal|"0,0"
decl_stmt|;
name|String
name|DEFAULT_VERSION
init|=
literal|"1.0"
decl_stmt|;
name|String
name|EMPTY
init|=
literal|""
decl_stmt|;
name|String
index|[]
name|SUPPORTED_PROTOCOL_VERSIONS
init|=
block|{
literal|"1.2"
block|,
literal|"1.1"
block|,
literal|"1.0"
block|}
decl_stmt|;
name|String
name|TEXT_PLAIN
init|=
literal|"text/plain"
decl_stmt|;
name|String
name|TRUE
init|=
literal|"true"
decl_stmt|;
name|String
name|FALSE
init|=
literal|"false"
decl_stmt|;
name|String
name|END
init|=
literal|"end"
decl_stmt|;
specifier|public
specifier|static
interface|interface
name|Commands
block|{
name|String
name|STOMP
init|=
literal|"STOMP"
decl_stmt|;
name|String
name|CONNECT
init|=
literal|"CONNECT"
decl_stmt|;
name|String
name|SEND
init|=
literal|"SEND"
decl_stmt|;
name|String
name|DISCONNECT
init|=
literal|"DISCONNECT"
decl_stmt|;
name|String
name|SUBSCRIBE
init|=
literal|"SUBSCRIBE"
decl_stmt|;
name|String
name|UNSUBSCRIBE
init|=
literal|"UNSUBSCRIBE"
decl_stmt|;
comment|// Preserve legacy incorrect allow shortened names for
comment|// subscribe and un-subscribe as it has been there for so
comment|// long that someone has undoubtedly come to expect it.
name|String
name|SUBSCRIBE_PREFIX
init|=
literal|"SUB"
decl_stmt|;
name|String
name|UNSUBSCRIBE_PREFIX
init|=
literal|"UNSUB"
decl_stmt|;
name|String
name|BEGIN_TRANSACTION
init|=
literal|"BEGIN"
decl_stmt|;
name|String
name|COMMIT_TRANSACTION
init|=
literal|"COMMIT"
decl_stmt|;
name|String
name|ABORT_TRANSACTION
init|=
literal|"ABORT"
decl_stmt|;
name|String
name|BEGIN
init|=
literal|"BEGIN"
decl_stmt|;
name|String
name|COMMIT
init|=
literal|"COMMIT"
decl_stmt|;
name|String
name|ABORT
init|=
literal|"ABORT"
decl_stmt|;
name|String
name|ACK
init|=
literal|"ACK"
decl_stmt|;
name|String
name|NACK
init|=
literal|"NACK"
decl_stmt|;
name|String
name|KEEPALIVE
init|=
literal|"KEEPALIVE"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Responses
block|{
name|String
name|CONNECTED
init|=
literal|"CONNECTED"
decl_stmt|;
name|String
name|ERROR
init|=
literal|"ERROR"
decl_stmt|;
name|String
name|MESSAGE
init|=
literal|"MESSAGE"
decl_stmt|;
name|String
name|RECEIPT
init|=
literal|"RECEIPT"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Headers
block|{
name|String
name|SEPERATOR
init|=
literal|":"
decl_stmt|;
name|String
name|RECEIPT_REQUESTED
init|=
literal|"receipt"
decl_stmt|;
name|String
name|TRANSACTION
init|=
literal|"transaction"
decl_stmt|;
name|String
name|CONTENT_LENGTH
init|=
literal|"content-length"
decl_stmt|;
name|String
name|CONTENT_TYPE
init|=
literal|"content-type"
decl_stmt|;
name|String
name|TRANSFORMATION
init|=
literal|"transformation"
decl_stmt|;
name|String
name|TRANSFORMATION_ERROR
init|=
literal|"transformation-error"
decl_stmt|;
comment|/**          * This header is used to instruct ActiveMQ to construct the message          * based with a specific type.          */
name|String
name|AMQ_MESSAGE_TYPE
init|=
literal|"amq-msg-type"
decl_stmt|;
specifier|public
interface|interface
name|Response
block|{
name|String
name|RECEIPT_ID
init|=
literal|"receipt-id"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Send
block|{
name|String
name|DESTINATION
init|=
literal|"destination"
decl_stmt|;
name|String
name|CORRELATION_ID
init|=
literal|"correlation-id"
decl_stmt|;
name|String
name|REPLY_TO
init|=
literal|"reply-to"
decl_stmt|;
name|String
name|EXPIRATION_TIME
init|=
literal|"expires"
decl_stmt|;
name|String
name|PRIORITY
init|=
literal|"priority"
decl_stmt|;
name|String
name|TYPE
init|=
literal|"type"
decl_stmt|;
name|String
name|PERSISTENT
init|=
literal|"persistent"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Message
block|{
name|String
name|MESSAGE_ID
init|=
literal|"message-id"
decl_stmt|;
name|String
name|ACK_ID
init|=
literal|"ack"
decl_stmt|;
name|String
name|DESTINATION
init|=
literal|"destination"
decl_stmt|;
name|String
name|CORRELATION_ID
init|=
literal|"correlation-id"
decl_stmt|;
name|String
name|EXPIRATION_TIME
init|=
literal|"expires"
decl_stmt|;
name|String
name|REPLY_TO
init|=
literal|"reply-to"
decl_stmt|;
name|String
name|PRORITY
init|=
literal|"priority"
decl_stmt|;
name|String
name|REDELIVERED
init|=
literal|"redelivered"
decl_stmt|;
name|String
name|TIMESTAMP
init|=
literal|"timestamp"
decl_stmt|;
name|String
name|TYPE
init|=
literal|"type"
decl_stmt|;
name|String
name|SUBSCRIPTION
init|=
literal|"subscription"
decl_stmt|;
name|String
name|BROWSER
init|=
literal|"browser"
decl_stmt|;
name|String
name|USERID
init|=
literal|"JMSXUserID"
decl_stmt|;
name|String
name|ORIGINAL_DESTINATION
init|=
literal|"original-destination"
decl_stmt|;
name|String
name|PERSISTENT
init|=
literal|"persistent"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Subscribe
block|{
name|String
name|DESTINATION
init|=
literal|"destination"
decl_stmt|;
name|String
name|ACK_MODE
init|=
literal|"ack"
decl_stmt|;
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
name|String
name|SELECTOR
init|=
literal|"selector"
decl_stmt|;
name|String
name|BROWSER
init|=
literal|"browser"
decl_stmt|;
specifier|public
interface|interface
name|AckModeValues
block|{
name|String
name|AUTO
init|=
literal|"auto"
decl_stmt|;
name|String
name|CLIENT
init|=
literal|"client"
decl_stmt|;
name|String
name|INDIVIDUAL
init|=
literal|"client-individual"
decl_stmt|;
block|}
block|}
specifier|public
interface|interface
name|Unsubscribe
block|{
name|String
name|DESTINATION
init|=
literal|"destination"
decl_stmt|;
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Connect
block|{
name|String
name|LOGIN
init|=
literal|"login"
decl_stmt|;
name|String
name|PASSCODE
init|=
literal|"passcode"
decl_stmt|;
name|String
name|CLIENT_ID
init|=
literal|"client-id"
decl_stmt|;
name|String
name|REQUEST_ID
init|=
literal|"request-id"
decl_stmt|;
name|String
name|ACCEPT_VERSION
init|=
literal|"accept-version"
decl_stmt|;
name|String
name|HOST
init|=
literal|"host"
decl_stmt|;
name|String
name|HEART_BEAT
init|=
literal|"heart-beat"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Error
block|{
name|String
name|MESSAGE
init|=
literal|"message"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Connected
block|{
name|String
name|SESSION
init|=
literal|"session"
decl_stmt|;
name|String
name|RESPONSE_ID
init|=
literal|"response-id"
decl_stmt|;
name|String
name|SERVER
init|=
literal|"server"
decl_stmt|;
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
name|String
name|HEART_BEAT
init|=
literal|"heart-beat"
decl_stmt|;
block|}
specifier|public
interface|interface
name|Ack
block|{
name|String
name|MESSAGE_ID
init|=
literal|"message-id"
decl_stmt|;
name|String
name|SUBSCRIPTION
init|=
literal|"subscription"
decl_stmt|;
name|String
name|ACK_ID
init|=
literal|"id"
decl_stmt|;
block|}
block|}
specifier|public
enum|enum
name|Transformations
block|{
name|JMS_BYTE
block|,
name|JMS_XML
block|,
name|JMS_JSON
block|,
name|JMS_OBJECT_XML
block|,
name|JMS_OBJECT_JSON
block|,
name|JMS_MAP_XML
block|,
name|JMS_MAP_JSON
block|,
name|JMS_ADVISORY_XML
block|,
name|JMS_ADVISORY_JSON
block|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"_"
argument_list|,
literal|"-"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Transformations
name|getValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|value
operator|.
name|replaceAll
argument_list|(
literal|"-"
argument_list|,
literal|"_"
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

