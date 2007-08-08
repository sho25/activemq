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
name|transport
operator|.
name|discovery
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
name|activemq
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DiscoveryAgentFactory
block|{
specifier|static
specifier|final
specifier|private
name|FactoryFinder
name|discoveryAgentFinder
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/transport/discoveryagent/"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
specifier|private
name|ConcurrentHashMap
name|discoveryAgentFactorys
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
comment|/**      * @param uri      * @return      * @throws IOException      */
specifier|private
specifier|static
name|DiscoveryAgentFactory
name|findDiscoveryAgentFactory
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|scheme
init|=
name|uri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheme
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"DiscoveryAgent scheme not specified: ["
operator|+
name|uri
operator|+
literal|"]"
argument_list|)
throw|;
name|DiscoveryAgentFactory
name|daf
init|=
operator|(
name|DiscoveryAgentFactory
operator|)
name|discoveryAgentFactorys
operator|.
name|get
argument_list|(
name|scheme
argument_list|)
decl_stmt|;
if|if
condition|(
name|daf
operator|==
literal|null
condition|)
block|{
comment|// Try to load if from a META-INF property.
try|try
block|{
name|daf
operator|=
operator|(
name|DiscoveryAgentFactory
operator|)
name|discoveryAgentFinder
operator|.
name|newInstance
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|discoveryAgentFactorys
operator|.
name|put
argument_list|(
name|scheme
argument_list|,
name|daf
argument_list|)
expr_stmt|;
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
literal|"DiscoveryAgent scheme NOT recognized: ["
operator|+
name|scheme
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|daf
return|;
block|}
specifier|public
specifier|static
name|DiscoveryAgent
name|createDiscoveryAgent
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|DiscoveryAgentFactory
name|tf
init|=
name|findDiscoveryAgentFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|doCreateDiscoveryAgent
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|abstract
specifier|protected
name|DiscoveryAgent
name|doCreateDiscoveryAgent
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|//    {
comment|//        try {
comment|//            String type = ( uri.getScheme() == null ) ? uri.getPath() : uri.getScheme();
comment|//            DiscoveryAgent rc = (DiscoveryAgent) discoveryAgentFinder.newInstance(type);
comment|//            Map options = URISupport.parseParamters(uri);
comment|//            IntrospectionSupport.setProperties(rc, options);
comment|//            if( rc.getClass() == SimpleDiscoveryAgent.class ) {
comment|//                CompositeData data = URISupport.parseComposite(uri);
comment|//                ((SimpleDiscoveryAgent)rc).setServices(data.getComponents());
comment|//            }
comment|//            return rc;
comment|//        } catch (Throwable e) {
comment|//            throw IOExceptionSupport.create("Could not create discovery agent: "+uri, e);
comment|//        }
comment|//    }
block|}
end_class

end_unit

