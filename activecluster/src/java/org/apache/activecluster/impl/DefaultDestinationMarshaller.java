begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|impl
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|activecluster
operator|.
name|DestinationMarshaller
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * A simple marshaller for Destinations  *  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|DefaultDestinationMarshaller
implements|implements
name|DestinationMarshaller
block|{
specifier|private
specifier|final
specifier|static
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DefaultDestinationMarshaller
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Builds a destination from a destinationName      * @param destinationName       *      * @return the destination to send messages to all members of the cluster      */
specifier|public
name|Destination
name|getDestination
parameter_list|(
name|String
name|destinationName
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
comment|/**      * Gets a destination's physical name      * @param destination      * @return the destination's physical name      */
specifier|public
name|String
name|getDestinationName
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Topic
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
try|try
block|{
name|result
operator|=
name|topic
operator|.
name|getTopicName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to get topic name for "
operator|+
name|destination
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|destination
decl_stmt|;
try|try
block|{
name|result
operator|=
name|queue
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to get queue name for "
operator|+
name|destination
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

