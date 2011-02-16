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
name|xmpp
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|Chat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|ChatManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|ChatManagerListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|ConnectionConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|PacketListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|XMPPConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|XMPPException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|filter
operator|.
name|PacketFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|packet
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smackx
operator|.
name|muc
operator|.
name|MultiUserChat
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|XmppTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
name|boolean
name|block
decl_stmt|;
specifier|private
specifier|final
name|XmppBroker
name|broker
init|=
operator|new
name|XmppBroker
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|long
name|sleepTime
init|=
literal|5000
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|block
operator|=
literal|true
expr_stmt|;
name|TestRunner
operator|.
name|run
argument_list|(
name|XmppTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionConfiguration
name|config
init|=
operator|new
name|ConnectionConfiguration
argument_list|(
literal|"localhost"
argument_list|,
literal|61222
argument_list|)
decl_stmt|;
comment|// config.setDebuggerEnabled(true);
try|try
block|{
comment|// SmackConfiguration.setPacketReplyTimeout(1000);
name|XMPPConnection
name|con
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|con
operator|.
name|connect
argument_list|()
expr_stmt|;
name|con
operator|.
name|login
argument_list|(
literal|"amq-user"
argument_list|,
literal|"amq-pwd"
argument_list|)
expr_stmt|;
name|ChatManager
name|chatManager
init|=
name|con
operator|.
name|getChatManager
argument_list|()
decl_stmt|;
name|Chat
name|chat
init|=
name|chatManager
operator|.
name|createChat
argument_list|(
literal|"test@localhost"
argument_list|,
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|processMessage
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got XMPP message from chat "
operator|+
name|chat
operator|.
name|getParticipant
argument_list|()
operator|+
literal|" message - "
operator|+
name|message
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|chat
operator|.
name|sendMessage
argument_list|(
literal|"Hello from Message: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent all messages!"
argument_list|)
expr_stmt|;
name|con
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMPPException
name|e
parameter_list|)
block|{
if|if
condition|(
name|block
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
if|if
condition|(
name|block
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Press any key to quit!: "
argument_list|)
expr_stmt|;
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done!"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testChat
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionConfiguration
name|config
init|=
operator|new
name|ConnectionConfiguration
argument_list|(
literal|"localhost"
argument_list|,
literal|61222
argument_list|)
decl_stmt|;
comment|//config.setDebuggerEnabled(true);
name|XMPPConnection
name|consumerCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|consumerCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|consumerCon
operator|.
name|login
argument_list|(
literal|"consumer"
argument_list|,
literal|"consumer"
argument_list|)
expr_stmt|;
name|consumerCon
operator|.
name|addPacketListener
argument_list|(
operator|new
name|XmppLogger
argument_list|(
literal|"CONSUMER INBOUND"
argument_list|)
argument_list|,
operator|new
name|PacketFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|consumerCon
operator|.
name|addPacketWriterListener
argument_list|(
operator|new
name|XmppLogger
argument_list|(
literal|"CONSUMER OUTBOUND"
argument_list|)
argument_list|,
operator|new
name|PacketFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|ConsumerMessageListener
name|listener
init|=
operator|new
name|ConsumerMessageListener
argument_list|()
decl_stmt|;
name|consumerCon
operator|.
name|getChatManager
argument_list|()
operator|.
name|addChatListener
argument_list|(
operator|new
name|ChatManagerListener
argument_list|()
block|{
specifier|public
name|void
name|chatCreated
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|boolean
name|createdLocally
parameter_list|)
block|{
name|chat
operator|.
name|addMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|XMPPConnection
name|producerCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|producerCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|producerCon
operator|.
name|login
argument_list|(
literal|"producer"
argument_list|,
literal|"producer"
argument_list|)
expr_stmt|;
name|producerCon
operator|.
name|addPacketListener
argument_list|(
operator|new
name|XmppLogger
argument_list|(
literal|"PRODUCER INBOUND"
argument_list|)
argument_list|,
operator|new
name|PacketFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|producerCon
operator|.
name|addPacketWriterListener
argument_list|(
operator|new
name|XmppLogger
argument_list|(
literal|"PRODUCER OUTBOUND"
argument_list|)
argument_list|,
operator|new
name|PacketFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Chat
name|chat
init|=
name|producerCon
operator|.
name|getChatManager
argument_list|()
operator|.
name|createChat
argument_list|(
literal|"consumer"
argument_list|,
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|processMessage
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got XMPP message from chat "
operator|+
name|chat
operator|.
name|getParticipant
argument_list|()
operator|+
literal|" message - "
operator|+
name|message
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
operator|new
name|Message
argument_list|(
literal|"consumer"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setType
argument_list|(
name|Message
operator|.
name|Type
operator|.
name|chat
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBody
argument_list|(
literal|"Hello from producer, message # "
operator|+
name|i
argument_list|)
expr_stmt|;
name|chat
operator|.
name|sendMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent all messages!"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumer received - "
operator|+
name|listener
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|listener
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultiUserChat
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n\n\n\n\n"
argument_list|)
expr_stmt|;
name|ConnectionConfiguration
name|config
init|=
operator|new
name|ConnectionConfiguration
argument_list|(
literal|"localhost"
argument_list|,
literal|61222
argument_list|)
decl_stmt|;
comment|//config.setDebuggerEnabled(true);
comment|//
name|XMPPConnection
name|consumerCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|consumerCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|consumerCon
operator|.
name|login
argument_list|(
literal|"consumer"
argument_list|,
literal|"consumer"
argument_list|)
expr_stmt|;
name|MultiUserChat
name|consumerMuc
init|=
operator|new
name|MultiUserChat
argument_list|(
name|consumerCon
argument_list|,
literal|"muc-test"
argument_list|)
decl_stmt|;
name|consumerMuc
operator|.
name|join
argument_list|(
literal|"consumer"
argument_list|)
expr_stmt|;
name|ConsumerMUCMessageListener
name|listener
init|=
operator|new
name|ConsumerMUCMessageListener
argument_list|()
decl_stmt|;
name|consumerMuc
operator|.
name|addMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|XMPPConnection
name|producerCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|producerCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|producerCon
operator|.
name|login
argument_list|(
literal|"producer"
argument_list|,
literal|"producer"
argument_list|)
expr_stmt|;
name|MultiUserChat
name|producerMuc
init|=
operator|new
name|MultiUserChat
argument_list|(
name|producerCon
argument_list|,
literal|"muc-test"
argument_list|)
decl_stmt|;
name|producerMuc
operator|.
name|join
argument_list|(
literal|"producer"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|producerMuc
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setBody
argument_list|(
literal|"Hello from producer, message # "
operator|+
name|i
argument_list|)
expr_stmt|;
name|producerMuc
operator|.
name|sendMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent all messages!"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumer received - "
operator|+
name|listener
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|listener
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addLoggingListeners
parameter_list|(
name|String
name|name
parameter_list|,
name|XMPPConnection
name|connection
parameter_list|)
block|{
name|connection
operator|.
name|addPacketListener
argument_list|(
operator|new
name|XmppLogger
argument_list|(
name|name
operator|+
literal|" INBOUND"
argument_list|)
argument_list|,
operator|new
name|PacketFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|addPacketWriterListener
argument_list|(
operator|new
name|XmppLogger
argument_list|(
name|name
operator|+
literal|" OUTBOUND"
argument_list|)
argument_list|,
operator|new
name|PacketFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTwoConnections
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n\n\n\n\n"
argument_list|)
expr_stmt|;
name|ConnectionConfiguration
name|config
init|=
operator|new
name|ConnectionConfiguration
argument_list|(
literal|"localhost"
argument_list|,
literal|61222
argument_list|)
decl_stmt|;
comment|//config.setDebuggerEnabled(true);
comment|//create the consumer first...
name|XMPPConnection
name|consumerCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|consumerCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|addLoggingListeners
argument_list|(
literal|"CONSUMER"
argument_list|,
name|consumerCon
argument_list|)
expr_stmt|;
name|consumerCon
operator|.
name|login
argument_list|(
literal|"consumer"
argument_list|,
literal|"consumer"
argument_list|)
expr_stmt|;
specifier|final
name|ConsumerMessageListener
name|listener1
init|=
operator|new
name|ConsumerMessageListener
argument_list|()
decl_stmt|;
name|consumerCon
operator|.
name|getChatManager
argument_list|()
operator|.
name|addChatListener
argument_list|(
operator|new
name|ChatManagerListener
argument_list|()
block|{
specifier|public
name|void
name|chatCreated
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|boolean
name|createdLocally
parameter_list|)
block|{
name|chat
operator|.
name|addMessageListener
argument_list|(
name|listener1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//now create the producer
name|XMPPConnection
name|producerCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Connecting producer and consumer"
argument_list|)
expr_stmt|;
name|producerCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|addLoggingListeners
argument_list|(
literal|"PRODUCER"
argument_list|,
name|producerCon
argument_list|)
expr_stmt|;
name|producerCon
operator|.
name|login
argument_list|(
literal|"producer"
argument_list|,
literal|"producer"
argument_list|)
expr_stmt|;
comment|//create the chat and send some messages
name|Chat
name|chat
init|=
name|producerCon
operator|.
name|getChatManager
argument_list|()
operator|.
name|createChat
argument_list|(
literal|"consumer"
argument_list|,
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|processMessage
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got XMPP message from chat "
operator|+
name|chat
operator|.
name|getParticipant
argument_list|()
operator|+
literal|" message - "
operator|+
name|message
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
operator|new
name|Message
argument_list|(
literal|"consumer"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setType
argument_list|(
name|Message
operator|.
name|Type
operator|.
name|chat
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBody
argument_list|(
literal|"Hello from producer, message # "
operator|+
name|i
argument_list|)
expr_stmt|;
name|chat
operator|.
name|sendMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|//make sure the consumer has time to receive all the messages...
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
comment|//create an identical 2nd consumer
name|XMPPConnection
name|lastguyCon
init|=
operator|new
name|XMPPConnection
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|lastguyCon
operator|.
name|connect
argument_list|()
expr_stmt|;
name|addLoggingListeners
argument_list|(
literal|"LASTGUY"
argument_list|,
name|consumerCon
argument_list|)
expr_stmt|;
name|lastguyCon
operator|.
name|login
argument_list|(
literal|"consumer"
argument_list|,
literal|"consumer"
argument_list|)
expr_stmt|;
specifier|final
name|ConsumerMessageListener
name|listener2
init|=
operator|new
name|ConsumerMessageListener
argument_list|()
decl_stmt|;
name|lastguyCon
operator|.
name|getChatManager
argument_list|()
operator|.
name|addChatListener
argument_list|(
operator|new
name|ChatManagerListener
argument_list|()
block|{
specifier|public
name|void
name|chatCreated
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|boolean
name|createdLocally
parameter_list|)
block|{
name|chat
operator|.
name|addMessageListener
argument_list|(
name|listener2
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sending message: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
operator|new
name|Message
argument_list|(
literal|"consumer"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setType
argument_list|(
name|Message
operator|.
name|Type
operator|.
name|chat
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBody
argument_list|(
literal|"Hello from producer, message # "
operator|+
name|i
argument_list|)
expr_stmt|;
name|chat
operator|.
name|sendMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent all messages!"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumer received - "
operator|+
name|listener1
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|listener1
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumer received - "
operator|+
name|listener2
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|listener2
operator|.
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
class|class
name|XmppLogger
implements|implements
name|PacketListener
block|{
specifier|private
specifier|final
name|String
name|direction
decl_stmt|;
specifier|public
name|XmppLogger
parameter_list|(
name|String
name|direction
parameter_list|)
block|{
name|this
operator|.
name|direction
operator|=
name|direction
expr_stmt|;
block|}
specifier|public
name|void
name|processPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|direction
operator|+
literal|" : "
operator|+
name|packet
operator|.
name|toXML
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|ConsumerMUCMessageListener
implements|implements
name|PacketListener
block|{
specifier|private
name|int
name|messageCount
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|processPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
if|if
condition|(
name|packet
operator|instanceof
name|Message
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received message number : "
operator|+
operator|(
name|messageCount
operator|++
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
block|}
class|class
name|ConsumerMessageListener
implements|implements
name|MessageListener
block|{
specifier|private
name|int
name|messageCount
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|processMessage
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received message number : "
operator|+
operator|(
name|messageCount
operator|++
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

