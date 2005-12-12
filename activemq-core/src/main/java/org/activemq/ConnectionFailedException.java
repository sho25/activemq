begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * An exception thrown when the a connection failure is detected (peer might close the connection, or a keep alive  * times out, etc.)  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionFailedException
extends|extends
name|JMSException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2288453203492073973L
decl_stmt|;
specifier|public
name|ConnectionFailedException
parameter_list|(
name|IOException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
literal|"The JMS connection has failed: "
operator|+
name|extractMessage
argument_list|(
name|cause
argument_list|)
argument_list|)
expr_stmt|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|setLinkedException
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|private
name|String
name|extractMessage
parameter_list|(
name|IOException
name|cause
parameter_list|)
block|{
name|String
name|m
init|=
name|cause
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
operator|||
name|m
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|m
operator|=
name|cause
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|m
return|;
block|}
block|}
end_class

end_unit

