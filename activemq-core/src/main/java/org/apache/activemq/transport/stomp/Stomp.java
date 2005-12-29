begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
specifier|public
specifier|static
interface|interface
name|Commands
block|{
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
literal|"SUB"
decl_stmt|;
name|String
name|UNSUBSCRIBE
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
block|}
block|}
block|}
end_interface

end_unit

