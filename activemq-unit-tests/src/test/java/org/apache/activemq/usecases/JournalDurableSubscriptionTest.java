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
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|journal
operator|.
name|JournalPersistenceAdapterFactory
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|JournalDurableSubscriptionTest
extends|extends
name|DurableSubscriptionTestSupport
block|{
specifier|protected
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-data/durableJournal"
argument_list|)
decl_stmt|;
name|JournalPersistenceAdapterFactory
name|factory
init|=
operator|new
name|JournalPersistenceAdapterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setDataDirectoryFile
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setUseJournal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setJournalLogFileSize
argument_list|(
literal|1024
operator|*
literal|64
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|createPersistenceAdapter
argument_list|()
return|;
block|}
block|}
end_class

end_unit

