begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|xnet
package|;
end_package

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|xnet
operator|.
name|hba
operator|.
name|IPAddressPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|gbean
operator|.
name|GBeanData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|gbean
operator|.
name|GBeanInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|gbean
operator|.
name|GBeanInfoBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|gbean
operator|.
name|GBeanLifecycle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|j2ee
operator|.
name|j2eeobjectnames
operator|.
name|NameFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|kernel
operator|.
name|GBeanAlreadyExistsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|kernel
operator|.
name|GBeanNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|kernel
operator|.
name|Kernel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|kernel
operator|.
name|jmx
operator|.
name|JMXUtil
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

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
name|InetSocketAddress
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
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|StandardServiceStackGBean
implements|implements
name|GBeanLifecycle
block|{
specifier|private
specifier|final
name|StandardServiceStack
name|standardServiceStack
decl_stmt|;
specifier|public
name|StandardServiceStackGBean
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|host
parameter_list|,
name|IPAddressPermission
index|[]
name|allowHosts
parameter_list|,
name|String
index|[]
name|logOnSuccess
parameter_list|,
name|String
index|[]
name|logOnFailure
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|ServerService
name|server
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|standardServiceStack
operator|=
operator|new
name|StandardServiceStack
argument_list|(
name|name
argument_list|,
name|port
argument_list|,
name|host
argument_list|,
name|allowHosts
argument_list|,
name|logOnSuccess
argument_list|,
name|logOnFailure
argument_list|,
name|executor
argument_list|,
name|server
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|InetAddress
name|getAddress
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getAddress
argument_list|()
return|;
block|}
specifier|public
name|InetSocketAddress
name|getFullAddress
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getFullAddress
argument_list|()
return|;
block|}
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getHost
argument_list|()
return|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getPort
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSoTimeout
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|standardServiceStack
operator|.
name|getSoTimeout
argument_list|()
return|;
block|}
specifier|public
name|void
name|setSoTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
throws|throws
name|SocketException
block|{
name|standardServiceStack
operator|.
name|setSoTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getLogOnSuccess
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getLogOnSuccess
argument_list|()
return|;
block|}
specifier|public
name|String
index|[]
name|getLogOnFailure
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getLogOnFailure
argument_list|()
return|;
block|}
specifier|public
name|IPAddressPermission
index|[]
name|getAllowHosts
parameter_list|()
block|{
return|return
name|standardServiceStack
operator|.
name|getAllowHosts
argument_list|()
return|;
block|}
specifier|public
name|void
name|setAllowHosts
parameter_list|(
name|IPAddressPermission
index|[]
name|allowHosts
parameter_list|)
block|{
name|standardServiceStack
operator|.
name|setAllowHosts
argument_list|(
name|allowHosts
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|standardServiceStack
operator|.
name|doStart
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doStop
parameter_list|()
throws|throws
name|Exception
block|{
name|standardServiceStack
operator|.
name|doStop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doFail
parameter_list|()
block|{
name|standardServiceStack
operator|.
name|doFail
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
specifier|final
name|GBeanInfo
name|GBEAN_INFO
decl_stmt|;
static|static
block|{
name|GBeanInfoBuilder
name|infoFactory
init|=
operator|new
name|GBeanInfoBuilder
argument_list|(
literal|"ActiveIO Connector"
argument_list|,
name|StandardServiceStackGBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"name"
argument_list|,
name|String
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"port"
argument_list|,
name|int
operator|.
name|class
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"soTimeout"
argument_list|,
name|int
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"host"
argument_list|,
name|String
operator|.
name|class
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"fullAddress"
argument_list|,
name|InetSocketAddress
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"allowHosts"
argument_list|,
name|IPAddressPermission
index|[]
operator|.
expr|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"logOnSuccess"
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addAttribute
argument_list|(
literal|"logOnFailure"
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addReference
argument_list|(
literal|"Executor"
argument_list|,
name|Executor
operator|.
name|class
argument_list|,
name|NameFactory
operator|.
name|GERONIMO_SERVICE
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|addReference
argument_list|(
literal|"Server"
argument_list|,
name|ServerService
operator|.
name|class
argument_list|,
name|NameFactory
operator|.
name|GERONIMO_SERVICE
argument_list|)
expr_stmt|;
name|infoFactory
operator|.
name|setConstructor
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"name"
block|,
literal|"port"
block|,
literal|"host"
block|,
literal|"allowHosts"
block|,
literal|"logOnSuccess"
block|,
literal|"logOnFailure"
block|,
literal|"Executor"
block|,
literal|"Server"
block|}
argument_list|)
expr_stmt|;
name|GBEAN_INFO
operator|=
name|infoFactory
operator|.
name|getBeanInfo
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|GBeanInfo
name|getGBeanInfo
parameter_list|()
block|{
return|return
name|GBEAN_INFO
return|;
block|}
specifier|public
specifier|static
name|ObjectName
name|addGBean
parameter_list|(
name|Kernel
name|kernel
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|host
parameter_list|,
name|InetAddress
index|[]
name|allowHosts
parameter_list|,
name|String
index|[]
name|logOnSuccess
parameter_list|,
name|String
index|[]
name|logOnFailure
parameter_list|,
name|ObjectName
name|executor
parameter_list|,
name|ObjectName
name|server
parameter_list|)
throws|throws
name|GBeanAlreadyExistsException
throws|,
name|GBeanNotFoundException
block|{
name|ClassLoader
name|classLoader
init|=
name|StandardServiceStack
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|ObjectName
name|SERVICE_NAME
init|=
name|JMXUtil
operator|.
name|getObjectName
argument_list|(
literal|"activeio:type=StandardServiceStack,name="
operator|+
name|name
argument_list|)
decl_stmt|;
name|GBeanData
name|gbean
init|=
operator|new
name|GBeanData
argument_list|(
name|SERVICE_NAME
argument_list|,
name|StandardServiceStackGBean
operator|.
name|GBEAN_INFO
argument_list|)
decl_stmt|;
name|gbean
operator|.
name|setAttribute
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setAttribute
argument_list|(
literal|"port"
argument_list|,
operator|new
name|Integer
argument_list|(
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setAttribute
argument_list|(
literal|"host"
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setAttribute
argument_list|(
literal|"allowHosts"
argument_list|,
name|allowHosts
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setAttribute
argument_list|(
literal|"logOnSuccess"
argument_list|,
name|logOnSuccess
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setAttribute
argument_list|(
literal|"logOnFailure"
argument_list|,
name|logOnFailure
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setReferencePattern
argument_list|(
literal|"Executor"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|gbean
operator|.
name|setReferencePattern
argument_list|(
literal|"Server"
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|kernel
operator|.
name|loadGBean
argument_list|(
name|gbean
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
name|kernel
operator|.
name|startGBean
argument_list|(
name|SERVICE_NAME
argument_list|)
expr_stmt|;
return|return
name|SERVICE_NAME
return|;
block|}
block|}
end_class

end_unit

