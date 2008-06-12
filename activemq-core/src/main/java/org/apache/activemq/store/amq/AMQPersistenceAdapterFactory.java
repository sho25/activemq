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
name|amq
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|AsyncDataManager
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
name|PersistenceAdapterFactory
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
name|ReferenceStoreAdapter
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|IOHelper
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link PersistenceAdapterFactory}  *   * @org.apache.xbean.XBean element="amqPersistenceAdapterFactory"  *   * @version $Revision: 1.17 $  */
end_comment

begin_class
specifier|public
class|class
name|AMQPersistenceAdapterFactory
implements|implements
name|PersistenceAdapterFactory
block|{
specifier|private
name|TaskRunnerFactory
name|taskRunnerFactory
decl_stmt|;
specifier|private
name|File
name|dataDirectory
decl_stmt|;
specifier|private
name|int
name|journalThreadPriority
init|=
name|Thread
operator|.
name|MAX_PRIORITY
decl_stmt|;
specifier|private
name|String
name|brokerName
init|=
literal|"localhost"
decl_stmt|;
specifier|private
name|ReferenceStoreAdapter
name|referenceStoreAdapter
decl_stmt|;
specifier|private
name|boolean
name|syncOnWrite
decl_stmt|;
specifier|private
name|boolean
name|syncOnTransaction
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|persistentIndex
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useNio
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|maxFileLength
init|=
name|AsyncDataManager
operator|.
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|private
name|long
name|cleanupInterval
init|=
name|AsyncDataManager
operator|.
name|DEFAULT_CLEANUP_INTERVAL
decl_stmt|;
comment|/**      * @return a AMQPersistenceAdapter      * @see org.apache.activemq.store.PersistenceAdapterFactory#createPersistenceAdapter()      */
specifier|public
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|()
block|{
name|AMQPersistenceAdapter
name|result
init|=
operator|new
name|AMQPersistenceAdapter
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDirectory
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setTaskRunnerFactory
argument_list|(
name|getTaskRunnerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setBrokerName
argument_list|(
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setSyncOnWrite
argument_list|(
name|isSyncOnWrite
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setPersistentIndex
argument_list|(
name|isPersistentIndex
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setReferenceStoreAdapter
argument_list|(
name|getReferenceStoreAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setUseNio
argument_list|(
name|isUseNio
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMaxFileLength
argument_list|(
name|getMaxFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setCleanupInterval
argument_list|(
name|getCleanupInterval
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|long
name|getCleanupInterval
parameter_list|()
block|{
return|return
name|cleanupInterval
return|;
block|}
specifier|public
name|void
name|setCleanupInterval
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|cleanupInterval
operator|=
name|val
expr_stmt|;
block|}
comment|/**      * @return the dataDirectory      */
specifier|public
name|File
name|getDataDirectory
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|dataDirectory
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|dataDirectory
operator|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|,
name|IOHelper
operator|.
name|toFileSystemSafeName
argument_list|(
name|brokerName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|dataDirectory
return|;
block|}
comment|/**      * @param dataDirectory the dataDirectory to set      */
specifier|public
name|void
name|setDataDirectory
parameter_list|(
name|File
name|dataDirectory
parameter_list|)
block|{
name|this
operator|.
name|dataDirectory
operator|=
name|dataDirectory
expr_stmt|;
block|}
comment|/**      * @return the taskRunnerFactory      */
specifier|public
name|TaskRunnerFactory
name|getTaskRunnerFactory
parameter_list|()
block|{
if|if
condition|(
name|taskRunnerFactory
operator|==
literal|null
condition|)
block|{
name|taskRunnerFactory
operator|=
operator|new
name|TaskRunnerFactory
argument_list|(
literal|"AMQPersistenceAdaptor Task"
argument_list|,
name|journalThreadPriority
argument_list|,
literal|true
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
return|return
name|taskRunnerFactory
return|;
block|}
comment|/**      * @param taskRunnerFactory the taskRunnerFactory to set      */
specifier|public
name|void
name|setTaskRunnerFactory
parameter_list|(
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|)
block|{
name|this
operator|.
name|taskRunnerFactory
operator|=
name|taskRunnerFactory
expr_stmt|;
block|}
comment|/**      * @return the journalThreadPriority      */
specifier|public
name|int
name|getJournalThreadPriority
parameter_list|()
block|{
return|return
name|this
operator|.
name|journalThreadPriority
return|;
block|}
comment|/**      * @param journalThreadPriority the journalThreadPriority to set      */
specifier|public
name|void
name|setJournalThreadPriority
parameter_list|(
name|int
name|journalThreadPriority
parameter_list|)
block|{
name|this
operator|.
name|journalThreadPriority
operator|=
name|journalThreadPriority
expr_stmt|;
block|}
comment|/**      * @return the brokerName      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|this
operator|.
name|brokerName
return|;
block|}
comment|/**      * @param brokerName the brokerName to set      */
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerName
operator|=
name|brokerName
expr_stmt|;
block|}
comment|/**      * @return the referenceStoreAdapter      */
specifier|public
name|ReferenceStoreAdapter
name|getReferenceStoreAdapter
parameter_list|()
block|{
return|return
name|this
operator|.
name|referenceStoreAdapter
return|;
block|}
comment|/**      * @param referenceStoreAdapter the referenceStoreAdapter to set      */
specifier|public
name|void
name|setReferenceStoreAdapter
parameter_list|(
name|ReferenceStoreAdapter
name|referenceStoreAdapter
parameter_list|)
block|{
name|this
operator|.
name|referenceStoreAdapter
operator|=
name|referenceStoreAdapter
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPersistentIndex
parameter_list|()
block|{
return|return
name|persistentIndex
return|;
block|}
specifier|public
name|void
name|setPersistentIndex
parameter_list|(
name|boolean
name|persistentIndex
parameter_list|)
block|{
name|this
operator|.
name|persistentIndex
operator|=
name|persistentIndex
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSyncOnWrite
parameter_list|()
block|{
return|return
name|syncOnWrite
return|;
block|}
specifier|public
name|void
name|setSyncOnWrite
parameter_list|(
name|boolean
name|syncOnWrite
parameter_list|)
block|{
name|this
operator|.
name|syncOnWrite
operator|=
name|syncOnWrite
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSyncOnTransaction
parameter_list|()
block|{
return|return
name|syncOnTransaction
return|;
block|}
specifier|public
name|void
name|setSyncOnTransaction
parameter_list|(
name|boolean
name|syncOnTransaction
parameter_list|)
block|{
name|this
operator|.
name|syncOnTransaction
operator|=
name|syncOnTransaction
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseNio
parameter_list|()
block|{
return|return
name|useNio
return|;
block|}
specifier|public
name|void
name|setUseNio
parameter_list|(
name|boolean
name|useNio
parameter_list|)
block|{
name|this
operator|.
name|useNio
operator|=
name|useNio
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxFileLength
parameter_list|()
block|{
return|return
name|maxFileLength
return|;
block|}
specifier|public
name|void
name|setMaxFileLength
parameter_list|(
name|int
name|maxFileLength
parameter_list|)
block|{
name|this
operator|.
name|maxFileLength
operator|=
name|maxFileLength
expr_stmt|;
block|}
block|}
end_class

end_unit

