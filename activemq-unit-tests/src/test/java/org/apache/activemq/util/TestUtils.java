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
name|util
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
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TestUtils
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|61616
decl_stmt|;
specifier|public
specifier|static
name|int
name|findOpenPort
parameter_list|()
block|{
return|return
name|findOpenPorts
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|findOpenPorts
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|ServerSocket
argument_list|>
name|sockets
init|=
operator|new
name|ArrayList
argument_list|<
name|ServerSocket
argument_list|>
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|ports
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|safeSet
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|count
argument_list|)
decl_stmt|;
comment|// Pre-fill with a sane default set.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
name|safeSet
operator|.
name|add
argument_list|(
name|DEFAULT_PORT
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
name|ServerSocket
name|socket
init|=
name|ServerSocketFactory
operator|.
name|getDefault
argument_list|()
operator|.
name|createServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|sockets
operator|.
name|add
argument_list|(
name|socket
argument_list|)
expr_stmt|;
name|ports
operator|.
name|add
argument_list|(
name|socket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|safeSet
return|;
block|}
finally|finally
block|{
for|for
control|(
name|ServerSocket
name|socket
range|:
name|sockets
control|)
block|{
try|try
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{}
block|}
block|}
return|return
name|ports
return|;
block|}
block|}
end_class

end_unit

