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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="110"  *   */
end_comment

begin_class
specifier|public
class|class
name|MessageId
implements|implements
name|DataStructure
implements|,
name|Comparable
argument_list|<
name|MessageId
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|MESSAGE_ID
decl_stmt|;
specifier|protected
name|ProducerId
name|producerId
decl_stmt|;
specifier|protected
name|long
name|producerSequenceId
decl_stmt|;
specifier|protected
name|long
name|brokerSequenceId
decl_stmt|;
specifier|private
specifier|transient
name|String
name|key
decl_stmt|;
specifier|private
specifier|transient
name|int
name|hashCode
decl_stmt|;
specifier|private
specifier|transient
name|AtomicReference
argument_list|<
name|Object
argument_list|>
name|dataLocator
init|=
operator|new
name|AtomicReference
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|transient
name|Object
name|entryLocator
decl_stmt|;
specifier|private
specifier|transient
name|Object
name|plistLocator
decl_stmt|;
specifier|public
name|MessageId
parameter_list|()
block|{
name|this
operator|.
name|producerId
operator|=
operator|new
name|ProducerId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MessageId
parameter_list|(
name|ProducerInfo
name|producerInfo
parameter_list|,
name|long
name|producerSequenceId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerInfo
operator|.
name|getProducerId
argument_list|()
expr_stmt|;
name|this
operator|.
name|producerSequenceId
operator|=
name|producerSequenceId
expr_stmt|;
block|}
specifier|public
name|MessageId
parameter_list|(
name|String
name|messageKey
parameter_list|)
block|{
name|setValue
argument_list|(
name|messageKey
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MessageId
parameter_list|(
name|String
name|producerId
parameter_list|,
name|long
name|producerSequenceId
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ProducerId
argument_list|(
name|producerId
argument_list|)
argument_list|,
name|producerSequenceId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MessageId
parameter_list|(
name|ProducerId
name|producerId
parameter_list|,
name|long
name|producerSequenceId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
name|this
operator|.
name|producerSequenceId
operator|=
name|producerSequenceId
expr_stmt|;
block|}
comment|/**      * Sets the value as a String      */
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|messageKey
parameter_list|)
block|{
name|key
operator|=
name|messageKey
expr_stmt|;
comment|// Parse off the sequenceId
name|int
name|p
init|=
name|messageKey
operator|.
name|lastIndexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>=
literal|0
condition|)
block|{
name|producerSequenceId
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|messageKey
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|messageKey
operator|=
name|messageKey
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
name|producerId
operator|=
operator|new
name|ProducerId
argument_list|(
name|messageKey
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the transient text view of the message which will be ignored if the      * message is marshaled on a transport; so is only for in-JVM changes to      * accommodate foreign JMS message IDs      */
specifier|public
name|void
name|setTextView
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|MessageId
name|id
init|=
operator|(
name|MessageId
operator|)
name|o
decl_stmt|;
return|return
name|producerSequenceId
operator|==
name|id
operator|.
name|producerSequenceId
operator|&&
name|producerId
operator|.
name|equals
argument_list|(
name|id
operator|.
name|producerId
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
literal|0
condition|)
block|{
name|hashCode
operator|=
name|producerId
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|int
operator|)
name|producerSequenceId
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
name|key
operator|=
name|producerId
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|producerSequenceId
expr_stmt|;
block|}
return|return
name|key
return|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ProducerId
name|getProducerId
parameter_list|()
block|{
return|return
name|producerId
return|;
block|}
specifier|public
name|void
name|setProducerId
parameter_list|(
name|ProducerId
name|producerId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getProducerSequenceId
parameter_list|()
block|{
return|return
name|producerSequenceId
return|;
block|}
specifier|public
name|void
name|setProducerSequenceId
parameter_list|(
name|long
name|producerSequenceId
parameter_list|)
block|{
name|this
operator|.
name|producerSequenceId
operator|=
name|producerSequenceId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|long
name|getBrokerSequenceId
parameter_list|()
block|{
return|return
name|brokerSequenceId
return|;
block|}
specifier|public
name|void
name|setBrokerSequenceId
parameter_list|(
name|long
name|brokerSequenceId
parameter_list|)
block|{
name|this
operator|.
name|brokerSequenceId
operator|=
name|brokerSequenceId
expr_stmt|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|MessageId
name|copy
parameter_list|()
block|{
name|MessageId
name|copy
init|=
operator|new
name|MessageId
argument_list|(
name|producerId
argument_list|,
name|producerSequenceId
argument_list|)
decl_stmt|;
name|copy
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|copy
operator|.
name|brokerSequenceId
operator|=
name|brokerSequenceId
expr_stmt|;
name|copy
operator|.
name|dataLocator
operator|=
name|dataLocator
expr_stmt|;
name|copy
operator|.
name|entryLocator
operator|=
name|entryLocator
expr_stmt|;
name|copy
operator|.
name|plistLocator
operator|=
name|plistLocator
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/**      * @param      * @return      * @see java.lang.Comparable#compareTo(java.lang.Object)      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|MessageId
name|other
parameter_list|)
block|{
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|this
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return a locator which aids a message store in loading a message faster.  Only used      * by the message stores.      */
specifier|public
name|Object
name|getDataLocator
parameter_list|()
block|{
return|return
name|dataLocator
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Sets a locator which aids a message store in loading a message faster.  Only used      * by the message stores.      */
specifier|public
name|void
name|setDataLocator
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|dataLocator
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|getEntryLocator
parameter_list|()
block|{
return|return
name|entryLocator
return|;
block|}
specifier|public
name|void
name|setEntryLocator
parameter_list|(
name|Object
name|entryLocator
parameter_list|)
block|{
name|this
operator|.
name|entryLocator
operator|=
name|entryLocator
expr_stmt|;
block|}
specifier|public
name|Object
name|getPlistLocator
parameter_list|()
block|{
return|return
name|plistLocator
return|;
block|}
specifier|public
name|void
name|setPlistLocator
parameter_list|(
name|Object
name|plistLocator
parameter_list|)
block|{
name|this
operator|.
name|plistLocator
operator|=
name|plistLocator
expr_stmt|;
block|}
block|}
end_class

end_unit

