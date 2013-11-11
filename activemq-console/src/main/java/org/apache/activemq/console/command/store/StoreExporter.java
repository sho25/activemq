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
name|console
operator|.
name|command
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageAck
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|SubscriptionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|XATransactionId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|command
operator|.
name|store
operator|.
name|proto
operator|.
name|MessagePB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|command
operator|.
name|store
operator|.
name|proto
operator|.
name|QueueEntryPB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|command
operator|.
name|store
operator|.
name|proto
operator|.
name|QueuePB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|MessageRecoveryListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|MessageStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|TopicMessageStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|TransactionRecoveryListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|AsciiBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|DataByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|UTF8Buffer
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|StoreExporter
block|{
specifier|static
specifier|final
name|int
name|OPENWIRE_VERSION
init|=
literal|8
decl_stmt|;
specifier|static
specifier|final
name|boolean
name|TIGHT_ENCODING
init|=
literal|false
decl_stmt|;
name|URI
name|config
decl_stmt|;
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AsciiBuffer
name|ds_kind
init|=
operator|new
name|AsciiBuffer
argument_list|(
literal|"ds"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AsciiBuffer
name|ptp_kind
init|=
operator|new
name|AsciiBuffer
argument_list|(
literal|"ptp"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AsciiBuffer
name|codec_id
init|=
operator|new
name|AsciiBuffer
argument_list|(
literal|"openwire"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|OpenWireFormat
name|wireformat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|public
name|StoreExporter
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|config
operator|=
operator|new
name|URI
argument_list|(
literal|"xbean:activemq.xml"
argument_list|)
expr_stmt|;
name|wireformat
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wireformat
operator|.
name|setTightEncodingEnabled
argument_list|(
name|TIGHT_ENCODING
argument_list|)
expr_stmt|;
name|wireformat
operator|.
name|setVersion
argument_list|(
name|OPENWIRE_VERSION
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"required --config option missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"required --file option missing"
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loading: "
operator|+
name|config
argument_list|)
expr_stmt|;
name|BrokerFactory
operator|.
name|setStartDefault
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// to avoid the broker auto-starting..
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|BrokerFactory
operator|.
name|resetStartDefault
argument_list|()
expr_stmt|;
name|PersistenceAdapter
name|store
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting: "
operator|+
name|store
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|BufferedOutputStream
name|fos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|export
argument_list|(
name|store
argument_list|,
name|fos
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|export
parameter_list|(
name|PersistenceAdapter
name|store
parameter_list|,
name|BufferedOutputStream
name|fos
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
index|[]
name|messageKeyCounter
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|}
decl_stmt|;
specifier|final
name|long
index|[]
name|containerKeyCounter
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|}
decl_stmt|;
specifier|final
name|ExportStreamManager
name|manager
init|=
operator|new
name|ExportStreamManager
argument_list|(
name|fos
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|preparedTxs
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|}
decl_stmt|;
name|store
operator|.
name|createTransactionStore
argument_list|()
operator|.
name|recover
argument_list|(
operator|new
name|TransactionRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|recover
parameter_list|(
name|XATransactionId
name|xid
parameter_list|,
name|Message
index|[]
name|addedMessages
parameter_list|,
name|MessageAck
index|[]
name|aks
parameter_list|)
block|{
name|preparedTxs
index|[
literal|0
index|]
operator|+=
literal|1
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|preparedTxs
index|[
literal|0
index|]
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Cannot export a store with prepared XA transactions.  Please commit or rollback those transactions before attempting to export."
argument_list|)
throw|;
block|}
for|for
control|(
name|ActiveMQDestination
name|odest
range|:
name|store
operator|.
name|getDestinations
argument_list|()
control|)
block|{
name|containerKeyCounter
index|[
literal|0
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|odest
operator|instanceof
name|ActiveMQQueue
condition|)
block|{
name|ActiveMQQueue
name|dest
init|=
operator|(
name|ActiveMQQueue
operator|)
name|odest
decl_stmt|;
name|MessageStore
name|queue
init|=
name|store
operator|.
name|createQueueMessageStore
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|QueuePB
operator|.
name|Bean
name|destRecord
init|=
operator|new
name|QueuePB
operator|.
name|Bean
argument_list|()
decl_stmt|;
name|destRecord
operator|.
name|setKey
argument_list|(
name|containerKeyCounter
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|destRecord
operator|.
name|setBindingKind
argument_list|(
name|ptp_kind
argument_list|)
expr_stmt|;
specifier|final
name|long
index|[]
name|seqKeyCounter
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|}
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jsonMap
operator|.
name|put
argument_list|(
literal|"@class"
argument_list|,
literal|"queue_destination"
argument_list|)
expr_stmt|;
name|jsonMap
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|dest
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|jsonMap
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|destRecord
operator|.
name|setBindingData
argument_list|(
operator|new
name|UTF8Buffer
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|store_queue
argument_list|(
name|destRecord
argument_list|)
expr_stmt|;
name|queue
operator|.
name|recover
argument_list|(
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|messageKeyCounter
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|seqKeyCounter
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|MessagePB
operator|.
name|Bean
name|messageRecord
init|=
name|createMessagePB
argument_list|(
name|message
argument_list|,
name|messageKeyCounter
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|manager
operator|.
name|store_message
argument_list|(
name|messageRecord
argument_list|)
expr_stmt|;
name|QueueEntryPB
operator|.
name|Bean
name|entryRecord
init|=
name|createQueueEntryPB
argument_list|(
name|message
argument_list|,
name|containerKeyCounter
index|[
literal|0
index|]
argument_list|,
name|seqKeyCounter
index|[
literal|0
index|]
argument_list|,
name|messageKeyCounter
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|manager
operator|.
name|store_queue_entry
argument_list|(
name|entryRecord
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|odest
operator|instanceof
name|ActiveMQTopic
condition|)
block|{
name|ActiveMQTopic
name|dest
init|=
operator|(
name|ActiveMQTopic
operator|)
name|odest
decl_stmt|;
name|TopicMessageStore
name|topic
init|=
name|store
operator|.
name|createTopicMessageStore
argument_list|(
name|dest
argument_list|)
decl_stmt|;
for|for
control|(
name|SubscriptionInfo
name|sub
range|:
name|topic
operator|.
name|getAllSubscriptions
argument_list|()
control|)
block|{
name|QueuePB
operator|.
name|Bean
name|destRecord
init|=
operator|new
name|QueuePB
operator|.
name|Bean
argument_list|()
decl_stmt|;
name|destRecord
operator|.
name|setKey
argument_list|(
name|containerKeyCounter
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|destRecord
operator|.
name|setBindingKind
argument_list|(
name|ds_kind
argument_list|)
expr_stmt|;
comment|// TODO: use a real JSON encoder like jackson.
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jsonMap
operator|.
name|put
argument_list|(
literal|"@class"
argument_list|,
literal|"dsub_destination"
argument_list|)
expr_stmt|;
name|jsonMap
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|sub
operator|.
name|getClientId
argument_list|()
operator|+
literal|":"
operator|+
name|sub
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonTopic
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|jsonTopic
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|)
expr_stmt|;
name|jsonMap
operator|.
name|put
argument_list|(
literal|"topics"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|jsonTopic
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|sub
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|jsonMap
operator|.
name|put
argument_list|(
literal|"selector"
argument_list|,
name|sub
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|json
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|jsonMap
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|destRecord
operator|.
name|setBindingData
argument_list|(
operator|new
name|UTF8Buffer
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|store_queue
argument_list|(
name|destRecord
argument_list|)
expr_stmt|;
specifier|final
name|long
name|seqKeyCounter
index|[]
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|}
decl_stmt|;
name|topic
operator|.
name|recoverSubscription
argument_list|(
name|sub
operator|.
name|getClientId
argument_list|()
argument_list|,
name|sub
operator|.
name|getSubscriptionName
argument_list|()
argument_list|,
operator|new
name|MessageRecoveryListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDuplicate
parameter_list|(
name|MessageId
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|messageKeyCounter
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|seqKeyCounter
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|MessagePB
operator|.
name|Bean
name|messageRecord
init|=
name|createMessagePB
argument_list|(
name|message
argument_list|,
name|messageKeyCounter
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|manager
operator|.
name|store_message
argument_list|(
name|messageRecord
argument_list|)
expr_stmt|;
name|QueueEntryPB
operator|.
name|Bean
name|entryRecord
init|=
name|createQueueEntryPB
argument_list|(
name|message
argument_list|,
name|containerKeyCounter
index|[
literal|0
index|]
argument_list|,
name|seqKeyCounter
index|[
literal|0
index|]
argument_list|,
name|messageKeyCounter
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|manager
operator|.
name|store_queue_entry
argument_list|(
name|entryRecord
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|manager
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
specifier|private
name|QueueEntryPB
operator|.
name|Bean
name|createQueueEntryPB
parameter_list|(
name|Message
name|message
parameter_list|,
name|long
name|queueKey
parameter_list|,
name|long
name|queueSeq
parameter_list|,
name|long
name|messageKey
parameter_list|)
block|{
name|QueueEntryPB
operator|.
name|Bean
name|entryRecord
init|=
operator|new
name|QueueEntryPB
operator|.
name|Bean
argument_list|()
decl_stmt|;
name|entryRecord
operator|.
name|setQueueKey
argument_list|(
name|queueKey
argument_list|)
expr_stmt|;
name|entryRecord
operator|.
name|setQueueSeq
argument_list|(
name|queueSeq
argument_list|)
expr_stmt|;
name|entryRecord
operator|.
name|setMessageKey
argument_list|(
name|messageKey
argument_list|)
expr_stmt|;
name|entryRecord
operator|.
name|setSize
argument_list|(
name|message
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getExpiration
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|entryRecord
operator|.
name|setExpiration
argument_list|(
name|message
operator|.
name|getExpiration
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getRedeliveryCounter
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|entryRecord
operator|.
name|setRedeliveries
argument_list|(
name|message
operator|.
name|getRedeliveryCounter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|entryRecord
return|;
block|}
specifier|private
name|MessagePB
operator|.
name|Bean
name|createMessagePB
parameter_list|(
name|Message
name|message
parameter_list|,
name|long
name|messageKey
parameter_list|)
throws|throws
name|IOException
block|{
name|DataByteArrayOutputStream
name|mos
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|mos
operator|.
name|writeBoolean
argument_list|(
name|TIGHT_ENCODING
argument_list|)
expr_stmt|;
name|mos
operator|.
name|writeVarInt
argument_list|(
name|OPENWIRE_VERSION
argument_list|)
expr_stmt|;
name|wireformat
operator|.
name|marshal
argument_list|(
name|message
argument_list|,
name|mos
argument_list|)
expr_stmt|;
name|MessagePB
operator|.
name|Bean
name|messageRecord
init|=
operator|new
name|MessagePB
operator|.
name|Bean
argument_list|()
decl_stmt|;
name|messageRecord
operator|.
name|setCodec
argument_list|(
name|codec_id
argument_list|)
expr_stmt|;
name|messageRecord
operator|.
name|setMessageKey
argument_list|(
name|messageKey
argument_list|)
expr_stmt|;
name|messageRecord
operator|.
name|setSize
argument_list|(
name|message
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|messageRecord
operator|.
name|setValue
argument_list|(
name|mos
operator|.
name|toBuffer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|messageRecord
return|;
block|}
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
specifier|public
name|void
name|setFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|setFile
argument_list|(
operator|new
name|File
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
specifier|public
name|URI
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
specifier|public
name|void
name|setConfig
parameter_list|(
name|URI
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
block|}
end_class

end_unit

