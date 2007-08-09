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
name|util
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_comment
comment|/**  * A comparator which works on SendCommand objects to compare the destinations  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MessageDestinationComparator
extends|extends
name|MessageComparatorSupport
block|{
specifier|protected
name|int
name|compareMessages
parameter_list|(
name|Message
name|message1
parameter_list|,
name|Message
name|message2
parameter_list|)
block|{
return|return
name|compareComparators
argument_list|(
name|getComparable
argument_list|(
name|getDestination
argument_list|(
name|message1
argument_list|)
argument_list|)
argument_list|,
name|getComparable
argument_list|(
name|getDestination
argument_list|(
name|message2
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|getDestination
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|instanceof
name|ActiveMQMessage
condition|)
block|{
name|ActiveMQMessage
name|amqMessage
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
decl_stmt|;
return|return
name|amqMessage
operator|.
name|getDestination
argument_list|()
return|;
block|}
try|try
block|{
return|return
name|message
operator|.
name|getJMSDestination
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|Comparable
name|getComparable
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
block|{
return|return
name|destination
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

