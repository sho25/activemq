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
name|broker
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|util
operator|.
name|FactoryFinder
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
name|IOExceptionSupport
import|;
end_import

begin_comment
comment|/**  * A helper class to create a fully configured broker service using a URI.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|BrokerFactory
block|{
specifier|static
specifier|final
specifier|private
name|FactoryFinder
name|brokerFactoryHandlerFinder
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/broker/"
argument_list|)
decl_stmt|;
specifier|public
interface|interface
name|BrokerFactoryHandler
block|{
specifier|public
name|BrokerService
name|createBroker
parameter_list|(
name|URI
name|brokerURI
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
specifier|public
specifier|static
name|BrokerFactoryHandler
name|createBrokerFactoryHandler
parameter_list|(
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|(
name|BrokerFactoryHandler
operator|)
name|brokerFactoryHandlerFinder
operator|.
name|newInstance
argument_list|(
name|type
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Could load "
operator|+
name|type
operator|+
literal|" factory:"
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Creates a broker from a URI configuration      * @param brokerURI      * @throws Exception       */
specifier|public
specifier|static
name|BrokerService
name|createBroker
parameter_list|(
name|URI
name|brokerURI
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerURI
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid broker URI, no scheme specified: "
operator|+
name|brokerURI
argument_list|)
throw|;
name|BrokerFactoryHandler
name|handler
init|=
name|createBrokerFactoryHandler
argument_list|(
name|brokerURI
operator|.
name|getScheme
argument_list|()
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
name|handler
operator|.
name|createBroker
argument_list|(
name|brokerURI
argument_list|)
decl_stmt|;
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

