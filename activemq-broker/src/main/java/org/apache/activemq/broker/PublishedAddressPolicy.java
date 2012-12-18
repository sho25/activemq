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
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|InetAddressUtil
import|;
end_import

begin_comment
comment|/**  * Policy object that controls how a TransportConnector publishes the connector's  * address to the outside world.  By default the connector will publish itself  * using the resolved host name of the bound server socket.  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|PublishedAddressPolicy
block|{
specifier|private
name|String
name|clusterClientUriQuery
decl_stmt|;
specifier|private
name|PublishedHostStrategy
name|publishedHostStrategy
init|=
name|PublishedHostStrategy
operator|.
name|DEFAULT
decl_stmt|;
comment|/**      * Defines the value of the published host value.      */
specifier|public
enum|enum
name|PublishedHostStrategy
block|{
name|DEFAULT
block|,
name|IPADDRESS
block|,
name|HOSTNAME
block|,
name|FQDN
block|;
specifier|public
specifier|static
name|PublishedHostStrategy
name|getValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|value
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Using the supplied TransportConnector this method returns the String that will      * be used to update clients with this connector's connect address.      *      * @param connector      *      The TransportConnector whose address is to be published.      * @return a string URI address that a client can use to connect to this Transport.      * @throws Exception      */
specifier|public
name|String
name|getPublishableConnectString
parameter_list|(
name|TransportConnector
name|connector
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|connectorURI
init|=
name|connector
operator|.
name|getConnectUri
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectorURI
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|scheme
init|=
name|connectorURI
operator|.
name|getScheme
argument_list|()
decl_stmt|;
name|String
name|userInfo
init|=
name|getPublishedUserInfoValue
argument_list|(
name|connectorURI
operator|.
name|getUserInfo
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|getPublishedHostValue
argument_list|(
name|connectorURI
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|connectorURI
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|getPublishedPathValue
argument_list|(
name|connectorURI
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fragment
init|=
name|getPublishedFragmentValue
argument_list|(
name|connectorURI
operator|.
name|getFragment
argument_list|()
argument_list|)
decl_stmt|;
name|URI
name|publishedURI
init|=
operator|new
name|URI
argument_list|(
name|scheme
argument_list|,
name|userInfo
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|path
argument_list|,
name|getClusterClientUriQuery
argument_list|()
argument_list|,
name|fragment
argument_list|)
decl_stmt|;
return|return
name|publishedURI
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Subclasses can override what host value is published by implementing alternate      * logic for this method.      *      * @param uriHostEntry      * @return      * @throws UnknownHostException      */
specifier|protected
name|String
name|getPublishedHostValue
parameter_list|(
name|String
name|uriHostEntry
parameter_list|)
throws|throws
name|UnknownHostException
block|{
comment|// By default we just republish what was already present.
name|String
name|result
init|=
name|uriHostEntry
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|publishedHostStrategy
operator|.
name|equals
argument_list|(
name|PublishedHostStrategy
operator|.
name|IPADDRESS
argument_list|)
condition|)
block|{
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|uriHostEntry
argument_list|)
decl_stmt|;
name|result
operator|=
name|address
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|publishedHostStrategy
operator|.
name|equals
argument_list|(
name|PublishedHostStrategy
operator|.
name|HOSTNAME
argument_list|)
condition|)
block|{
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|uriHostEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|address
operator|.
name|isAnyLocalAddress
argument_list|()
condition|)
block|{
comment|// make it more human readable and useful, an alternative to 0.0.0.0
name|result
operator|=
name|InetAddressUtil
operator|.
name|getLocalHostName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|address
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|publishedHostStrategy
operator|.
name|equals
argument_list|(
name|PublishedHostStrategy
operator|.
name|FQDN
argument_list|)
condition|)
block|{
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|uriHostEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|address
operator|.
name|isAnyLocalAddress
argument_list|()
condition|)
block|{
comment|// make it more human readable and useful, an alternative to 0.0.0.0
name|result
operator|=
name|InetAddressUtil
operator|.
name|getLocalHostName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|address
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Subclasses can override what path value is published by implementing alternate      * logic for this method.  By default this method simply returns what was already      * set as the Path value in the original URI.      *      * @param uriPathEntry      *      The original value of the URI path.      *      * @return the desired value for the published URI's path.      */
specifier|protected
name|String
name|getPublishedPathValue
parameter_list|(
name|String
name|uriPathEntry
parameter_list|)
block|{
return|return
name|uriPathEntry
return|;
block|}
comment|/**      * Subclasses can override what host value is published by implementing alternate      * logic for this method.  By default this method simply returns what was already      * set as the Fragment value in the original URI.      *      * @param uriFragmentEntry      *      The original value of the URI Fragment.      *      * @return the desired value for the published URI's Fragment.      */
specifier|protected
name|String
name|getPublishedFragmentValue
parameter_list|(
name|String
name|uriFragmentEntry
parameter_list|)
block|{
return|return
name|uriFragmentEntry
return|;
block|}
comment|/**      * Subclasses can override what user info value is published by implementing alternate      * logic for this method.  By default this method simply returns what was already      * set as the UserInfo value in the original URI.      *      * @param uriUserInfoEntry      *      The original value of the URI user info.      *      * @return the desired value for the published URI's user info.      */
specifier|protected
name|String
name|getPublishedUserInfoValue
parameter_list|(
name|String
name|uriUserInfoEntry
parameter_list|)
block|{
return|return
name|uriUserInfoEntry
return|;
block|}
comment|/**      * Gets the URI query that's configured on the published URI that's sent to client's      * when the cluster info is updated.      *      * @return the clusterClientUriQuery      */
specifier|public
name|String
name|getClusterClientUriQuery
parameter_list|()
block|{
return|return
name|clusterClientUriQuery
return|;
block|}
comment|/**      * Sets the URI query that's configured on the published URI that's sent to client's      * when the cluster info is updated.      *      * @param clusterClientUriQuery the clusterClientUriQuery to set      */
specifier|public
name|void
name|setClusterClientUriQuery
parameter_list|(
name|String
name|clusterClientUriQuery
parameter_list|)
block|{
name|this
operator|.
name|clusterClientUriQuery
operator|=
name|clusterClientUriQuery
expr_stmt|;
block|}
comment|/**      * @return the publishedHostStrategy      */
specifier|public
name|PublishedHostStrategy
name|getPublishedHostStrategy
parameter_list|()
block|{
return|return
name|publishedHostStrategy
return|;
block|}
comment|/**      * @param publishedHostStrategy the publishedHostStrategy to set      */
specifier|public
name|void
name|setPublishedHostStrategy
parameter_list|(
name|PublishedHostStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|publishedHostStrategy
operator|=
name|strategy
expr_stmt|;
block|}
comment|/**      * @param publishedHostStrategy the publishedHostStrategy to set      */
specifier|public
name|void
name|setPublishedHostStrategy
parameter_list|(
name|String
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|publishedHostStrategy
operator|=
name|PublishedHostStrategy
operator|.
name|getValue
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

