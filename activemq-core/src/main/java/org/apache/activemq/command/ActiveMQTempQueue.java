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
name|TemporaryQueue
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="102"  * @version $Revision: 1.6 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQTempQueue
extends|extends
name|ActiveMQTempDestination
implements|implements
name|TemporaryQueue
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6683049467527633867L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|ACTIVEMQ_TEMP_QUEUE
decl_stmt|;
specifier|public
name|ActiveMQTempQueue
parameter_list|()
block|{     }
specifier|public
name|ActiveMQTempQueue
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
name|ActiveMQTempQueue
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
name|getConnectionId
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
name|isQueue
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|getQueueName
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
name|TEMP_QUEUE_TYPE
return|;
block|}
specifier|protected
name|String
name|getQualifiedPrefix
parameter_list|()
block|{
return|return
name|TEMP_QUEUE_QUALIFED_PREFIX
return|;
block|}
block|}
end_class

end_unit

