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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|ProducerId
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
name|util
operator|.
name|BitArrayBin
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
name|util
operator|.
name|IdGenerator
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
name|util
operator|.
name|LRUCache
import|;
end_import

begin_comment
comment|/**  * Provides basic audit functions for Messages  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQMessageAudit
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_WINDOW_SIZE
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAXIMUM_PRODUCER_COUNT
init|=
literal|128
decl_stmt|;
specifier|private
name|int
name|windowSize
decl_stmt|;
specifier|private
name|LinkedHashMap
argument_list|<
name|Object
argument_list|,
name|BitArrayBin
argument_list|>
name|map
decl_stmt|;
comment|/**      * Default Constructor windowSize = 1024, maximumNumberOfProducersToTrack =      * 128      */
specifier|public
name|ActiveMQMessageAudit
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_WINDOW_SIZE
argument_list|,
name|MAXIMUM_PRODUCER_COUNT
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a MessageAudit      *       * @param windowSize range of ids to track      * @param maximumNumberOfProducersToTrack number of producers expected in      *                the system      */
specifier|public
name|ActiveMQMessageAudit
parameter_list|(
name|int
name|windowSize
parameter_list|,
specifier|final
name|int
name|maximumNumberOfProducersToTrack
parameter_list|)
block|{
name|this
operator|.
name|windowSize
operator|=
name|windowSize
expr_stmt|;
name|map
operator|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|BitArrayBin
argument_list|>
argument_list|(
name|maximumNumberOfProducersToTrack
argument_list|,
name|maximumNumberOfProducersToTrack
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if this message has beeb seen before      *       * @param message      * @return true if the message is a duplicate      * @throws JMSException      */
specifier|public
name|boolean
name|isDuplicateMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|isDuplicate
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * checks whether this messageId has been seen before and adds this      * messageId to the list      *       * @param id      * @return true if the message is a duplicate      */
specifier|public
specifier|synchronized
name|boolean
name|isDuplicate
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|boolean
name|answer
init|=
literal|false
decl_stmt|;
name|String
name|seed
init|=
name|IdGenerator
operator|.
name|getSeedFromId
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
name|BitArrayBin
name|bab
init|=
name|map
operator|.
name|get
argument_list|(
name|seed
argument_list|)
decl_stmt|;
if|if
condition|(
name|bab
operator|==
literal|null
condition|)
block|{
name|bab
operator|=
operator|new
name|BitArrayBin
argument_list|(
name|windowSize
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|seed
argument_list|,
name|bab
argument_list|)
expr_stmt|;
block|}
name|long
name|index
init|=
name|IdGenerator
operator|.
name|getSequenceFromId
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|answer
operator|=
name|bab
operator|.
name|setBit
argument_list|(
name|index
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Checks if this message has beeb seen before      *       * @param message      * @return true if the message is a duplicate      */
specifier|public
specifier|synchronized
name|boolean
name|isDuplicateMessageReference
parameter_list|(
specifier|final
name|MessageReference
name|message
parameter_list|)
block|{
name|boolean
name|answer
init|=
literal|false
decl_stmt|;
name|MessageId
name|id
init|=
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|ProducerId
name|pid
init|=
name|id
operator|.
name|getProducerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|pid
operator|!=
literal|null
condition|)
block|{
name|BitArrayBin
name|bab
init|=
name|map
operator|.
name|get
argument_list|(
name|pid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bab
operator|==
literal|null
condition|)
block|{
name|bab
operator|=
operator|new
name|BitArrayBin
argument_list|(
name|windowSize
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|pid
argument_list|,
name|bab
argument_list|)
expr_stmt|;
block|}
name|answer
operator|=
name|bab
operator|.
name|setBit
argument_list|(
name|id
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
comment|/**      * uun mark this messager as being received      *       * @param message      */
specifier|public
specifier|synchronized
name|void
name|rollbackMessageReference
parameter_list|(
specifier|final
name|MessageReference
name|message
parameter_list|)
block|{
name|MessageId
name|id
init|=
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|ProducerId
name|pid
init|=
name|id
operator|.
name|getProducerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|pid
operator|!=
literal|null
condition|)
block|{
name|BitArrayBin
name|bab
init|=
name|map
operator|.
name|get
argument_list|(
name|pid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bab
operator|!=
literal|null
condition|)
block|{
name|bab
operator|.
name|setBit
argument_list|(
name|id
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

