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
name|transport
operator|.
name|discovery
operator|.
name|simple
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|transport
operator|.
name|discovery
operator|.
name|DiscoveryAgent
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
name|transport
operator|.
name|discovery
operator|.
name|DiscoveryAgentFactory
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
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
name|URISupport
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
name|URISupport
operator|.
name|CompositeData
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleDiscoveryAgentFactory
extends|extends
name|DiscoveryAgentFactory
block|{
specifier|protected
name|DiscoveryAgent
name|doCreateDiscoveryAgent
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|Map
name|options
init|=
name|URISupport
operator|.
name|parseParamters
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|SimpleDiscoveryAgent
name|rc
init|=
operator|new
name|SimpleDiscoveryAgent
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setGroup
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|rc
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setServices
argument_list|(
name|data
operator|.
name|getComponents
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
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
literal|"Could not create discovery agent: "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

