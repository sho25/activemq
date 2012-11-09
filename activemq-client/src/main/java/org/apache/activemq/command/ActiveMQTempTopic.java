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
name|TemporaryTopic
import|;
end_import

begin_comment
comment|/**  * @org.apache.xbean.XBean element="tempTopic" description="An ActiveMQ Temporary Topic Destination"  * @openwire:marshaller code="103"  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQTempTopic
extends|extends
name|ActiveMQTempDestination
implements|implements
name|TemporaryTopic
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|ACTIVEMQ_TEMP_TOPIC
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4325596784597300253L
decl_stmt|;
specifier|public
name|ActiveMQTempTopic
parameter_list|()
block|{     }
specifier|public
name|ActiveMQTempTopic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQTempTopic
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|,
name|long
name|sequenceId
parameter_list|)
block|{
name|super
argument_list|(
name|connectionId
operator|.
name|getValue
argument_list|()
argument_list|,
name|sequenceId
argument_list|)
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
name|isTopic
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|getTopicName
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getPhysicalName
argument_list|()
return|;
block|}
specifier|public
name|byte
name|getDestinationType
parameter_list|()
block|{
return|return
name|TEMP_TOPIC_TYPE
return|;
block|}
specifier|protected
name|String
name|getQualifiedPrefix
parameter_list|()
block|{
return|return
name|TEMP_TOPIC_QUALIFED_PREFIX
return|;
block|}
block|}
end_class

end_unit
