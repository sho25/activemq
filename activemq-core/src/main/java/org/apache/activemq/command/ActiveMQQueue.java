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
name|Queue
import|;
end_import

begin_comment
comment|/**  *   * @org.apache.xbean.XBean element="queue" description="An ActiveMQ Queue  *                         Destination"  *   * @openwire:marshaller code="100"  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQQueue
extends|extends
name|ActiveMQDestination
implements|implements
name|Queue
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3885260014960795889L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|ACTIVEMQ_QUEUE
decl_stmt|;
specifier|public
name|ActiveMQQueue
parameter_list|()
block|{     }
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
name|ActiveMQQueue
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
name|QUEUE_TYPE
return|;
block|}
specifier|protected
name|String
name|getQualifiedPrefix
parameter_list|()
block|{
return|return
name|QUEUE_QUALIFIED_PREFIX
return|;
block|}
block|}
end_class

end_unit

