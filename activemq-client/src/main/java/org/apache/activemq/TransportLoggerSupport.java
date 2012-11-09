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
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|TransportLoggerSupport
block|{
specifier|public
specifier|static
name|String
name|defaultLogWriterName
init|=
literal|"default"
decl_stmt|;
specifier|public
specifier|static
interface|interface
name|SPI
block|{
specifier|public
name|Transport
name|createTransportLogger
parameter_list|(
name|Transport
name|transport
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
name|Transport
name|createTransportLogger
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|String
name|logWriterName
parameter_list|,
name|boolean
name|dynamicManagement
parameter_list|,
name|boolean
name|startLogging
parameter_list|,
name|int
name|jmxPort
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
specifier|final
specifier|static
specifier|public
name|SPI
name|spi
decl_stmt|;
static|static
block|{
name|SPI
name|temp
decl_stmt|;
try|try
block|{
name|temp
operator|=
operator|(
name|SPI
operator|)
name|TransportLoggerSupport
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"org.apache.activemq.transport.TransportLoggerFactorySPI"
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|temp
operator|=
literal|null
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|//To change body of catch statement use File | Settings | File Templates.
block|}
name|spi
operator|=
name|temp
expr_stmt|;
block|}
specifier|public
specifier|static
name|Transport
name|createTransportLogger
parameter_list|(
name|Transport
name|transport
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|spi
operator|!=
literal|null
condition|)
block|{
return|return
name|spi
operator|.
name|createTransportLogger
argument_list|(
name|transport
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|transport
return|;
block|}
block|}
specifier|public
specifier|static
name|Transport
name|createTransportLogger
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|String
name|logWriterName
parameter_list|,
name|boolean
name|dynamicManagement
parameter_list|,
name|boolean
name|startLogging
parameter_list|,
name|int
name|jmxPort
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|spi
operator|!=
literal|null
condition|)
block|{
return|return
name|spi
operator|.
name|createTransportLogger
argument_list|(
name|transport
argument_list|,
name|logWriterName
argument_list|,
name|dynamicManagement
argument_list|,
name|startLogging
argument_list|,
name|jmxPort
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|transport
return|;
block|}
block|}
block|}
end_class

end_unit
