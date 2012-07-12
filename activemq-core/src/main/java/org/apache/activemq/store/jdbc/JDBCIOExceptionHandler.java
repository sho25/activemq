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
name|store
operator|.
name|jdbc
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|DefaultIOExceptionHandler
import|;
end_import

begin_comment
comment|/**  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|JDBCIOExceptionHandler
extends|extends
name|DefaultIOExceptionHandler
block|{
specifier|public
name|JDBCIOExceptionHandler
parameter_list|()
block|{
name|setIgnoreSQLExceptions
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setStopStartConnectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|hasLockOwnership
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|hasLock
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|instanceof
name|JDBCPersistenceAdapter
condition|)
block|{
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
init|=
operator|(
name|JDBCPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|DatabaseLocker
name|locker
init|=
name|jdbcPersistenceAdapter
operator|.
name|getDatabaseLocker
argument_list|()
decl_stmt|;
if|if
condition|(
name|locker
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|locker
operator|.
name|keepAlive
argument_list|()
condition|)
block|{
name|hasLock
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{                 }
if|if
condition|(
operator|!
name|hasLock
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"PersistenceAdapter lock no longer valid using: "
operator|+
name|locker
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|hasLock
return|;
block|}
block|}
end_class

end_unit
