begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|camel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
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
name|EmbeddedBrokerTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Endpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|ProducerTemplate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|mock
operator|.
name|MockEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultCamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|util
operator|.
name|jndi
operator|.
name|JndiContext
import|;
end_import

begin_comment
comment|/**  * A helper class for test cases which use an embedded broker and use Camel to  * do the routing  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CamelEmbeddedBrokerTestSupport
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|protected
name|CamelContext
name|camelContext
decl_stmt|;
specifier|protected
name|ProducerTemplate
name|template
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:61616"
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|camelContext
operator|=
name|createCamelContext
argument_list|()
expr_stmt|;
name|addCamelRoutes
argument_list|(
name|camelContext
argument_list|)
expr_stmt|;
name|assertValidContext
argument_list|(
name|camelContext
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|start
argument_list|()
expr_stmt|;
name|template
operator|=
name|camelContext
operator|.
name|createProducerTemplate
argument_list|()
expr_stmt|;
name|template
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|template
operator|!=
literal|null
condition|)
block|{
name|template
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|camelContext
operator|!=
literal|null
condition|)
block|{
name|camelContext
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|CamelContext
name|createCamelContext
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|DefaultCamelContext
argument_list|(
name|createJndiContext
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|protected
name|Context
name|createJndiContext
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|JndiContext
argument_list|(
operator|new
name|Hashtable
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|void
name|addCamelRoutes
parameter_list|(
name|CamelContext
name|camelContext
parameter_list|)
throws|throws
name|Exception
block|{     }
comment|/**      * Resolves a mandatory endpoint for the given URI or an exception is thrown      *      * @param uri      *            the Camel<a href="">URI</a> to use to create or resolve an      *            endpoint      * @return the endpoint      */
specifier|protected
name|Endpoint
name|resolveMandatoryEndpoint
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
name|resolveMandatoryEndpoint
argument_list|(
name|camelContext
argument_list|,
name|uri
argument_list|)
return|;
block|}
comment|/**      * Resolves a mandatory endpoint for the given URI and expected type or an      * exception is thrown      *      * @param uri      *            the Camel<a href="">URI</a> to use to create or resolve an      *            endpoint      * @return the endpoint      */
specifier|protected
parameter_list|<
name|T
extends|extends
name|Endpoint
parameter_list|>
name|T
name|resolveMandatoryEndpoint
parameter_list|(
name|String
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|endpointType
parameter_list|)
block|{
return|return
name|resolveMandatoryEndpoint
argument_list|(
name|camelContext
argument_list|,
name|uri
argument_list|,
name|endpointType
argument_list|)
return|;
block|}
comment|/**      * Resolves an endpoint and asserts that it is found      */
specifier|protected
name|Endpoint
name|resolveMandatoryEndpoint
parameter_list|(
name|CamelContext
name|context
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|Endpoint
name|endpoint
init|=
name|context
operator|.
name|getEndpoint
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No endpoint found for URI: "
operator|+
name|uri
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
return|return
name|endpoint
return|;
block|}
comment|/**      * Resolves an endpoint and asserts that it is found      */
specifier|protected
parameter_list|<
name|T
extends|extends
name|Endpoint
parameter_list|>
name|T
name|resolveMandatoryEndpoint
parameter_list|(
name|CamelContext
name|context
parameter_list|,
name|String
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|endpointType
parameter_list|)
block|{
name|T
name|endpoint
init|=
name|context
operator|.
name|getEndpoint
argument_list|(
name|uri
argument_list|,
name|endpointType
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No endpoint found for URI: "
operator|+
name|uri
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
return|return
name|endpoint
return|;
block|}
comment|/**      * Resolves the mandatory Mock endpoint using a URI of the form      *<code>mock:someName</code>      *      * @param uri      *            the URI which typically starts with "mock:" and has some name      * @return the mandatory mock endpoint or an exception is thrown if it could      *         not be resolved      */
specifier|protected
name|MockEndpoint
name|getMockEndpoint
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
name|resolveMandatoryEndpoint
argument_list|(
name|uri
argument_list|,
name|MockEndpoint
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * Asserts that all the expectations of the Mock endpoints are valid      */
specifier|protected
name|void
name|assertMockEndpointsSatisifed
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MockEndpoint
operator|.
name|assertIsSatisfied
argument_list|(
name|camelContext
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertValidContext
parameter_list|(
name|CamelContext
name|context
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"No context found!"
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

