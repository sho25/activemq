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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|CompositeTransport
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
name|Transport
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
name|TransportServer
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
name|failover
operator|.
name|FailoverTransport
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
name|failover
operator|.
name|FailoverTransportFactory
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
operator|.
name|CompositeData
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DiscoveryTransportFactory
extends|extends
name|FailoverTransportFactory
block|{
specifier|public
name|Transport
name|createTransport
parameter_list|(
name|CompositeData
name|compositeData
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|compositeData
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|FailoverTransport
name|failoverTransport
init|=
name|createTransport
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
return|return
name|createTransport
argument_list|(
name|failoverTransport
argument_list|,
name|compositeData
argument_list|)
return|;
block|}
comment|/**      * Creates a transport that reports discovered brokers to a specific composite transport.      *       * @param compositeTransport transport to report discovered brokers to      * @param compositeData used to apply parameters to this transport       * @return a transport that reports discovered brokers to a specific composite transport.      * @throws IOException      */
specifier|public
specifier|static
name|DiscoveryTransport
name|createTransport
parameter_list|(
name|CompositeTransport
name|compositeTransport
parameter_list|,
name|CompositeData
name|compositeData
parameter_list|)
throws|throws
name|IOException
block|{
name|DiscoveryTransport
name|transport
init|=
operator|new
name|DiscoveryTransport
argument_list|(
name|compositeTransport
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|compositeData
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|transport
operator|.
name|setParameters
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|URI
name|discoveryAgentURI
init|=
name|compositeData
operator|.
name|getComponents
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|DiscoveryAgent
name|discoveryAgent
init|=
name|DiscoveryAgentFactory
operator|.
name|createDiscoveryAgent
argument_list|(
name|discoveryAgentURI
argument_list|)
decl_stmt|;
name|transport
operator|.
name|setDiscoveryAgent
argument_list|(
name|discoveryAgent
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
specifier|public
name|TransportServer
name|doBind
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid server URI: "
operator|+
name|location
argument_list|)
throw|;
comment|// try{
comment|//            CompositeData compositData=URISupport.parseComposite(location);
comment|//            URI[] components=compositData.getComponents();
comment|//            if(components.length!=1){
comment|//                throw new IOException("Invalid location: "+location
comment|//                                +", the location must have 1 and only 1 composite URI in it - components = "
comment|//                                +components.length);
comment|//            }
comment|//            Map parameters=new HashMap(compositData.getParameters());
comment|//            DiscoveryTransportServer server=new DiscoveryTransportServer(TransportFactory.bind(value,components[0]));
comment|//            IntrospectionSupport.setProperties(server,parameters,"discovery");
comment|//            DiscoveryAgent discoveryAgent=DiscoveryAgentFactory.createDiscoveryAgent(server.getDiscovery());
comment|//            // Use the host name to configure the group of the discovery agent.
comment|//            if(!parameters.containsKey("discovery.group")){
comment|//                if(compositData.getHost()!=null){
comment|//                    parameters.put("discovery.group",compositData.getHost());
comment|//                }
comment|//            }
comment|//            if(!parameters.containsKey("discovery.brokerName")){
comment|//                parameters.put("discovery.brokerName",value);
comment|//            }
comment|//            IntrospectionSupport.setProperties(discoveryAgent,parameters,"discovery.");
comment|//            server.setDiscoveryAgent(discoveryAgent);
comment|//            return server;
comment|//        }catch(URISyntaxException e){
comment|//            throw new IOException("Invalid location: "+location);
comment|//        }
block|}
block|}
end_class

end_unit

