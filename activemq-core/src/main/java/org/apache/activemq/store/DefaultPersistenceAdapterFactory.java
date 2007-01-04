begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|activeio
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|active
operator|.
name|JournalImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|active
operator|.
name|JournalLockedException
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
name|jdbc
operator|.
name|DataSourceSupport
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
name|jdbc
operator|.
name|JDBCAdapter
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
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|jdbc
operator|.
name|Statements
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
name|JournalPersistenceAdapter
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
name|kahadaptor
operator|.
name|KahaPersistenceAdapter
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * Factory class that can create PersistenceAdapter objects.  *  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|DefaultPersistenceAdapterFactory
extends|extends
name|DataSourceSupport
implements|implements
name|PersistenceAdapterFactory
block|{
specifier|private
specifier|static
specifier|final
name|int
name|JOURNAL_LOCKED_WAIT_DELAY
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DefaultPersistenceAdapterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|journalLogFileSize
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|20
decl_stmt|;
specifier|private
name|int
name|journalLogFiles
init|=
literal|2
decl_stmt|;
specifier|private
name|TaskRunnerFactory
name|taskRunnerFactory
decl_stmt|;
specifier|private
name|Journal
name|journal
decl_stmt|;
specifier|private
name|boolean
name|useJournal
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useQuickJournal
init|=
literal|false
decl_stmt|;
specifier|private
name|File
name|journalArchiveDirectory
decl_stmt|;
specifier|private
name|boolean
name|failIfJournalIsLocked
init|=
literal|false
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
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
specifier|public
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|()
throws|throws
name|IOException
block|{
name|jdbcPersistenceAdapter
operator|.
name|setDataSource
argument_list|(
name|getDataSource
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|useJournal
condition|)
block|{
return|return
name|jdbcPersistenceAdapter
return|;
block|}
comment|// Setup the Journal
comment|//        if( useQuickJournal ) {
comment|//            return new QuickJournalPersistenceAdapter(getJournal(), jdbcPersistenceAdapter, getTaskRunnerFactory());
comment|//        }  else {
name|KahaPersistenceAdapter
name|adaptor
init|=
operator|new
name|KahaPersistenceAdapter
argument_list|(
operator|new
name|File
argument_list|(
literal|"amqstore"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|JournalPersistenceAdapter
argument_list|(
name|getJournal
argument_list|()
argument_list|,
name|jdbcPersistenceAdapter
argument_list|,
name|getTaskRunnerFactory
argument_list|()
argument_list|)
return|;
comment|//return new JournalPersistenceAdapter(getJournal(), adaptor, getTaskRunnerFactory());
comment|//        }
block|}
specifier|public
name|int
name|getJournalLogFiles
parameter_list|()
block|{
return|return
name|journalLogFiles
return|;
block|}
comment|/**      * Sets the number of journal log files to use      */
specifier|public
name|void
name|setJournalLogFiles
parameter_list|(
name|int
name|journalLogFiles
parameter_list|)
block|{
name|this
operator|.
name|journalLogFiles
operator|=
name|journalLogFiles
expr_stmt|;
block|}
specifier|public
name|int
name|getJournalLogFileSize
parameter_list|()
block|{
return|return
name|journalLogFileSize
return|;
block|}
comment|/**      * Sets the size of the journal log files      *      * @org.apache.xbean.Property propertyEditor="org.apache.activemq.util.MemoryIntPropertyEditor"      */
specifier|public
name|void
name|setJournalLogFileSize
parameter_list|(
name|int
name|journalLogFileSize
parameter_list|)
block|{
name|this
operator|.
name|journalLogFileSize
operator|=
name|journalLogFileSize
expr_stmt|;
block|}
specifier|public
name|JDBCPersistenceAdapter
name|getJdbcAdapter
parameter_list|()
block|{
return|return
name|jdbcPersistenceAdapter
return|;
block|}
specifier|public
name|void
name|setJdbcAdapter
parameter_list|(
name|JDBCPersistenceAdapter
name|jdbcAdapter
parameter_list|)
block|{
name|this
operator|.
name|jdbcPersistenceAdapter
operator|=
name|jdbcAdapter
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseJournal
parameter_list|()
block|{
return|return
name|useJournal
return|;
block|}
comment|/**      * Enables or disables the use of the journal. The default is to use the journal      *      * @param useJournal      */
specifier|public
name|void
name|setUseJournal
parameter_list|(
name|boolean
name|useJournal
parameter_list|)
block|{
name|this
operator|.
name|useJournal
operator|=
name|useJournal
expr_stmt|;
block|}
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
literal|"Persistence Adaptor Task"
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
specifier|public
name|Journal
name|getJournal
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|journal
operator|==
literal|null
condition|)
block|{
name|createJournal
argument_list|()
expr_stmt|;
block|}
return|return
name|journal
return|;
block|}
specifier|public
name|void
name|setJournal
parameter_list|(
name|Journal
name|journal
parameter_list|)
block|{
name|this
operator|.
name|journal
operator|=
name|journal
expr_stmt|;
block|}
specifier|public
name|File
name|getJournalArchiveDirectory
parameter_list|()
block|{
if|if
condition|(
name|journalArchiveDirectory
operator|==
literal|null
operator|&&
name|useQuickJournal
condition|)
block|{
name|journalArchiveDirectory
operator|=
operator|new
name|File
argument_list|(
name|getDataDirectoryFile
argument_list|()
argument_list|,
literal|"journal"
argument_list|)
expr_stmt|;
block|}
return|return
name|journalArchiveDirectory
return|;
block|}
specifier|public
name|void
name|setJournalArchiveDirectory
parameter_list|(
name|File
name|journalArchiveDirectory
parameter_list|)
block|{
name|this
operator|.
name|journalArchiveDirectory
operator|=
name|journalArchiveDirectory
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseQuickJournal
parameter_list|()
block|{
return|return
name|useQuickJournal
return|;
block|}
comment|/**      * Enables or disables the use of quick journal, which keeps messages in the journal and just      * stores a reference to the messages in JDBC. Defaults to false so that messages actually reside      * long term in the JDBC database.      */
specifier|public
name|void
name|setUseQuickJournal
parameter_list|(
name|boolean
name|useQuickJournal
parameter_list|)
block|{
name|this
operator|.
name|useQuickJournal
operator|=
name|useQuickJournal
expr_stmt|;
block|}
specifier|public
name|JDBCAdapter
name|getAdapter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|jdbcPersistenceAdapter
operator|.
name|getAdapter
argument_list|()
return|;
block|}
specifier|public
name|void
name|setAdapter
parameter_list|(
name|JDBCAdapter
name|adapter
parameter_list|)
block|{
name|jdbcPersistenceAdapter
operator|.
name|setAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Statements
name|getStatements
parameter_list|()
block|{
return|return
name|jdbcPersistenceAdapter
operator|.
name|getStatements
argument_list|()
return|;
block|}
specifier|public
name|void
name|setStatements
parameter_list|(
name|Statements
name|statements
parameter_list|)
block|{
name|jdbcPersistenceAdapter
operator|.
name|setStatements
argument_list|(
name|statements
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseDatabaseLock
parameter_list|()
block|{
return|return
name|jdbcPersistenceAdapter
operator|.
name|isUseDatabaseLock
argument_list|()
return|;
block|}
comment|/**      * Sets whether or not an exclusive database lock should be used to enable JDBC Master/Slave. Enabled by default.      */
specifier|public
name|void
name|setUseDatabaseLock
parameter_list|(
name|boolean
name|useDatabaseLock
parameter_list|)
block|{
name|jdbcPersistenceAdapter
operator|.
name|setUseDatabaseLock
argument_list|(
name|useDatabaseLock
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCreateTablesOnStartup
parameter_list|()
block|{
return|return
name|jdbcPersistenceAdapter
operator|.
name|isCreateTablesOnStartup
argument_list|()
return|;
block|}
comment|/**      * Sets whether or not tables are created on startup      */
specifier|public
name|void
name|setCreateTablesOnStartup
parameter_list|(
name|boolean
name|createTablesOnStartup
parameter_list|)
block|{
name|jdbcPersistenceAdapter
operator|.
name|setCreateTablesOnStartup
argument_list|(
name|createTablesOnStartup
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getJournalThreadPriority
parameter_list|()
block|{
return|return
name|journalThreadPriority
return|;
block|}
comment|/**      * Sets the thread priority of the journal thread      */
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
comment|/**      * @throws IOException      */
specifier|protected
name|void
name|createJournal
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|journalDir
init|=
operator|new
name|File
argument_list|(
name|getDataDirectoryFile
argument_list|()
argument_list|,
literal|"journal"
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|failIfJournalIsLocked
condition|)
block|{
name|journal
operator|=
operator|new
name|JournalImpl
argument_list|(
name|journalDir
argument_list|,
name|journalLogFiles
argument_list|,
name|journalLogFileSize
argument_list|,
name|getJournalArchiveDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|journal
operator|=
operator|new
name|JournalImpl
argument_list|(
name|journalDir
argument_list|,
name|journalLogFiles
argument_list|,
name|journalLogFileSize
argument_list|,
name|getJournalArchiveDirectory
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|JournalLockedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Journal is locked... waiting "
operator|+
operator|(
name|JOURNAL_LOCKED_WAIT_DELAY
operator|/
literal|1000
operator|)
operator|+
literal|" seconds for the journal to be unlocked."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|JOURNAL_LOCKED_WAIT_DELAY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{                     }
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

