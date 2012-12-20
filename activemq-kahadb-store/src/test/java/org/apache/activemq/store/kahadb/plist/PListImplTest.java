begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|kahadb
operator|.
name|plist
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
name|store
operator|.
name|PListStore
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
name|PListTestSupport
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|PListImplTest
extends|extends
name|PListTestSupport
block|{
annotation|@
name|Override
specifier|protected
name|PListStoreImpl
name|createPListStore
parameter_list|()
block|{
return|return
operator|new
name|PListStoreImpl
argument_list|()
return|;
block|}
specifier|protected
name|PListStore
name|createConcurrentAddIteratePListStore
parameter_list|()
block|{
name|PListStoreImpl
name|store
init|=
name|createPListStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setIndexPageSize
argument_list|(
literal|2
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|store
operator|.
name|setCleanupInterval
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|store
operator|.
name|setIndexEnablePageCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|store
operator|.
name|setIndexWriteBatchSize
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PListStore
name|createConcurrentAddRemovePListStore
parameter_list|()
block|{
name|PListStoreImpl
name|store
init|=
name|createPListStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setCleanupInterval
argument_list|(
literal|400
argument_list|)
expr_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|5
argument_list|)
expr_stmt|;
name|store
operator|.
name|setLazyInit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PListStore
name|createConcurrentAddRemoveWithPreloadPListStore
parameter_list|()
block|{
name|PListStoreImpl
name|store
init|=
name|createPListStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|1024
operator|*
literal|5
argument_list|)
expr_stmt|;
name|store
operator|.
name|setCleanupInterval
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|store
operator|.
name|setIndexWriteBatchSize
argument_list|(
literal|500
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PListStore
name|createConcurrentAddIterateRemovePListStore
parameter_list|(
name|boolean
name|enablePageCache
parameter_list|)
block|{
name|PListStoreImpl
name|store
init|=
name|createPListStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setIndexEnablePageCaching
argument_list|(
name|enablePageCache
argument_list|)
expr_stmt|;
name|store
operator|.
name|setIndexPageSize
argument_list|(
literal|2
operator|*
literal|1024
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
block|}
end_class

end_unit
