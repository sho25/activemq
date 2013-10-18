begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2006 the original author or authors.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jms
operator|.
name|pool
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_class
specifier|public
class|class
name|JcaPooledConnectionFactory
extends|extends
name|XaPooledConnectionFactory
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|protected
name|ConnectionPool
name|createConnectionPool
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|JcaConnectionPool
argument_list|(
name|connection
argument_list|,
name|getTransactionManager
argument_list|()
argument_list|,
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
