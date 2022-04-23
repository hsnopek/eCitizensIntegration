import React, {useEffect} from 'react';

function SimulateAuthorizationServicePage(props) {

    useEffect(() => {
        document.form.submit();
    }, [])

    return (
        <form name='form' action='http://localhost:8080/authorization/getFormData' method='post'>
            <input type='hidden' name='ServiceRequest'
                   value='PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U2VydmljZVJlcXVlc3QgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgSWQ9Il8yZWMwODkzYmI1ZWY0MGVkODUwZWRkMjk1OTYxNTY3NCIgRXhwaXJ5VGltZT0iMjAyMC0xMS0wNVQwNzo0NzoxNS4yMjQ2MDc5KzAxOjAwIiB4bWxucz0iaHR0cDovL2Vvdmxhc3RlbmphLmZpbmEuaHIvYXV0aG9yaXphdGlvbmRvY3VtZW50L3YzIj48QXV0aG9yaXphdGlvbkluZm8+PFNlcnZpY2VTdWJqZWN0TmFtZT5DTj1UZXN0IFNlcnZpcyAyLCBMPVpBR1JFQiwgT0lELjIuNS40Ljk3PUhSODU4MjExMzAzNjgsIE89RklOQSwgQz1IUjwvU2VydmljZVN1YmplY3ROYW1lPjxGcm9tRW50aXR5PjxQZXJzb24+PExvY2FsUGVyc29uIHhtbG5zPSJodHRwOi8vZW92bGFzdGVuamEuZmluYS5oci9hdXRob3JpemF0aW9uYmFzZS92MiI+PE9JQj40MTg4MDYwMjQ1NDwvT0lCPjxGaXJzdE5hbWU+TUFSSU5LTzwvRmlyc3ROYW1lPjxMYXN0TmFtZT5NUkFLIFNUQU5JxIY8L0xhc3ROYW1lPjwvTG9jYWxQZXJzb24+PC9QZXJzb24+PExlZ2FsPjxOYW1lIHhtbG5zPSJodHRwOi8vZW92bGFzdGVuamEuZmluYS5oci9hdXRob3JpemF0aW9uYmFzZS92MiI+RklOQU5DSUpTS0EgQUdFTkNJSkE8L05hbWU+PEppcHMgeG1sbnM9Imh0dHA6Ly9lb3ZsYXN0ZW5qYS5maW5hLmhyL2F1dGhvcml6YXRpb25iYXNlL3YyIj48SVBTPjg1ODIxMTMwMzY4PC9JUFM+PElaVk9SX1JFRz4xPC9JWlZPUl9SRUc+PC9KaXBzPjwvTGVnYWw+PC9Gcm9tRW50aXR5PjxGb3JFbnRpdHk+PExlZ2FsIHhtbG5zPSJodHRwOi8vZW92bGFzdGVuamEuZmluYS5oci9hdXRob3JpemF0aW9uYmFzZS92MiI+PE5hbWU+RklOQU5DSUpTS0EgQUdFTkNJSkE8L05hbWU+PEppcHM+PElQUz44NTgyMTEzMDM2ODwvSVBTPjxJWlZPUl9SRUc+MTwvSVpWT1JfUkVHPjwvSmlwcz48L0xlZ2FsPjwvRm9yRW50aXR5PjxUb0VudGl0eT48Q2VydGlmaWNhdGVETiAvPjxQZXJzb24+PE9JQiB4bWxucz0iaHR0cDovL2Vvdmxhc3RlbmphLmZpbmEuaHIvYXV0aG9yaXphdGlvbmJhc2UvdjIiPjQxODgwNjAyNDU0PC9PSUI+PEZpcnN0TmFtZSB4bWxucz0iaHR0cDovL2Vvdmxhc3RlbmphLmZpbmEuaHIvYXV0aG9yaXphdGlvbmJhc2UvdjIiPk1BUklOS088L0ZpcnN0TmFtZT48TGFzdE5hbWUgeG1sbnM9Imh0dHA6Ly9lb3ZsYXN0ZW5qYS5maW5hLmhyL2F1dGhvcml6YXRpb25iYXNlL3YyIj5NUkFLIFNUQU5JxIY8L0xhc3ROYW1lPjwvUGVyc29uPjxMZWdhbD48TmFtZSB4bWxucz0iaHR0cDovL2Vvdmxhc3RlbmphLmZpbmEuaHIvYXV0aG9yaXphdGlvbmJhc2UvdjIiPkZJTkFOQ0lKU0tBIEFHRU5DSUpBPC9OYW1lPjxKaXBzIHhtbG5zPSJodHRwOi8vZW92bGFzdGVuamEuZmluYS5oci9hdXRob3JpemF0aW9uYmFzZS92MiI+PElQUz44NTgyMTEzMDM2ODwvSVBTPjxJWlZPUl9SRUc+MTwvSVpWT1JfUkVHPjwvSmlwcz48L0xlZ2FsPjxFbWFpbCAvPjwvVG9FbnRpdHk+PFZhbGlkRnJvbT4yMDIwLTExLTA1VDAwOjAwOjAwKzAxOjAwPC9WYWxpZEZyb20+PEFjdGl2ZVBlcm1pc3Npb25zPjxQZXJtaXNzaW9uPjxLZXk+VUxPR0E8L0tleT48VmFsdWU+YWRtaW48L1ZhbHVlPjxEZXNjcmlwdGlvbj5SYXppbmEgcHJpc3R1cGE8L0Rlc2NyaXB0aW9uPjxWYWx1ZURlc2NyaXB0aW9uPkFkbWluaXN0cmF0b3I8L1ZhbHVlRGVzY3JpcHRpb24+PC9QZXJtaXNzaW9uPjxQZXJtaXNzaW9uPjxLZXk+UFJBVk88L0tleT48VmFsdWU+cmVhZDwvVmFsdWU+PERlc2NyaXB0aW9uPk92bGFzdGk8L0Rlc2NyaXB0aW9uPjxWYWx1ZURlc2NyaXB0aW9uPsSMaXRhbmplPC9WYWx1ZURlc2NyaXB0aW9uPjwvUGVybWlzc2lvbj48UGVybWlzc2lvbj48S2V5PlBEVjwvS2V5PjxWYWx1ZT5UcnVlPC9WYWx1ZT48RGVzY3JpcHRpb24+UHJhdm8gcHJlZGFqZSBQRFYgb2JyYXNjYTwvRGVzY3JpcHRpb24+PFZhbHVlRGVzY3JpcHRpb24+RGE8L1ZhbHVlRGVzY3JpcHRpb24+PC9QZXJtaXNzaW9uPjwvQWN0aXZlUGVybWlzc2lvbnM+PC9BdXRob3JpemF0aW9uSW5mbz48VGVtcGxhdGVJbmZvPjxMZWdhbERvY3VtZW50VHlwZT5QUklTVFVQPC9MZWdhbERvY3VtZW50VHlwZT48SXNEaXJlY3Q+dHJ1ZTwvSXNEaXJlY3Q+PElzUmVmZXJlbnQ+ZmFsc2U8L0lzUmVmZXJlbnQ+PC9UZW1wbGF0ZUluZm8+PFNpZ25hdHVyZXM+PFNpZ25hdHVyZSBJZD0iUm9SZXF1ZXN0U2lnbmF0dXJlIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+PFNpZ25lZEluZm8+PENhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiIC8+PFNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZHNpZy1tb3JlI3JzYS1zaGEyNTYiIC8+PFJlZmVyZW5jZSBVUkk9IiNfMmVjMDg5M2JiNWVmNDBlZDg1MGVkZDI5NTk2MTU2NzQiPjxUcmFuc2Zvcm1zPjxUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIgLz48VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIiAvPjwvVHJhbnNmb3Jtcz48RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiIC8+PERpZ2VzdFZhbHVlPkRjTDFmS2VlWW1qdmtkcWFmWk9DT2FBSHlKbz08L0RpZ2VzdFZhbHVlPjwvUmVmZXJlbmNlPjwvU2lnbmVkSW5mbz48U2lnbmF0dXJlVmFsdWU+TERBSjF2R1FSUnQ1cEt5UDRZaFVzb3E2SnNBbXFmZytQc09OeFQ5bWg4aCtiS2VFbW5oMTlSQjkwZTRjNTFUY1lqNDM0OUFrQldmSXNvaEI4emtGQ2x3ZlBWNFAzUHN6d0hLejhiOHJUalpOYUhuS3hnKzh3R3JEMTZNb0pZbkNqRms0ZklzYkkwSGhnR2tJRTRuU2wvSDQwTkw5c2t3LzdTeVJFQWZNamVSbU5xMWlUdktzWWZDNlkreWpJSlNzd3lsamdKdC9rTmg3bThZcUxidlRsOVM4YUVGYTBsTVlKeXR4NVhTbkg5NTZ0bjNHV3pLa3hGNnBvbm5wT1JZYWpzc1dIdVNKNFV0YVVXU0Y0VVlQcUdxeWxkcFU5WWM2Wm85Ukg1ZEllejBPUFZiR0dlTWJnTjNXOEFDZG1UTVNaOThkYTF4N0RTcVlydlN2bTdEemN3PT08L1NpZ25hdHVyZVZhbHVlPjxLZXlJbmZvPjxLZXlOYW1lPkNOPVVwcmF2bGphbmplLWVPdmxhc3RlbmppbWFUc3QsIEw9WkFHUkVCLCBPSUQuMi41LjQuOTc9SFI4NTgyMTEzMDM2OCwgTz1GSU5BLCBDPUhSPC9LZXlOYW1lPjxLZXlWYWx1ZT48UlNBS2V5VmFsdWU+PE1vZHVsdXM+MlRTZXF1a1E3cmd5ZVR1VEJvanBuZGVwS0t4Rm4xbS9ZTEk3c1BaMjlCcldzWXBJc2c4RlcvZVVZQk1kRFl6ODdTSXRMcEZhZFB1SnNjVGdRcXpnUFF2enh5MnFYUFg1ZFVtYWh6WEd2ODdiTnRIbGExNmVSL3Zza0F1WG5yUi9RVXdub2IxUFdaRUtqSzh2QXV3TW9jUnpEQkNoUmhXOXJBK0tZUmp3MkNTV2R2QVBGRTIyaFJISjhpTFVDSFV6NzZsL2xJMXBEWHkwZUlVaWdTRDV2cjBCYU1HT1RpODFRMGdic1ozQTZSbTRTZm9xaGgrLzRDVVR2S2tUWndKaU1JdlJ3Z3RSUWxTUWNnbGQ2eTVWZS80SnpHaDJ5VmZtL1Z1OU15akNMWVFtTFc2ZnpzRWFzd1J4Y3FPOUV6WTRXbk1QY3dQSFhBUHhURXVqRVRmWFp3PT08L01vZHVsdXM+PEV4cG9uZW50PkFRQUI8L0V4cG9uZW50PjwvUlNBS2V5VmFsdWU+PC9LZXlWYWx1ZT48WDUwOURhdGE+PFg1MDlDZXJ0aWZpY2F0ZT5NSUlITURDQ0JSaWdBd0lCQWdJUkFMdDUvcGo4TlJRUUFBQUFBRjJsZnJRd0RRWUpLb1pJaHZjTkFRRUxCUUF3U0RFTE1Ba0dBMVVFQmhNQ1NGSXhIVEFiQmdOVkJBb1RGRVpwYm1GdVkybHFjMnRoSUdGblpXNWphV3BoTVJvd0dBWURWUVFERXhGR2FXNWhJRVJsYlc4Z1EwRWdNakF4TkRBZUZ3MHlNREF6TURVd09UQTVNRFphRncweU1qQXpNRFV3T1RBNU1EWmFNR3d4Q3pBSkJnTlZCQVlUQWtoU01RMHdDd1lEVlFRS0V3UkdTVTVCTVJZd0ZBWURWUVJoRXcxSVVqZzFPREl4TVRNd016WTRNUTh3RFFZRFZRUUhFd1phUVVkU1JVSXhKVEFqQmdOVkJBTVRIRlZ3Y21GMmJHcGhibXBsTFdWUGRteGhjM1JsYm1wcGJXRlVjM1F3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRRFpOSjZxNlJEdXVESjVPNU1HaU9tZDE2a29yRVdmV2I5Z3NqdXc5bmIwR3RheGlraXlEd1ZiOTVSZ0V4ME5qUHp0SWkwdWtWcDArNG14eE9CQ3JPQTlDL1BITGFwYzlmbDFTWnFITmNhL3p0czIwZVZyWHA1SCsreVFDNWVldEg5QlRDZWh2VTlaa1FxTXJ5OEM3QXloeEhNTUVLRkdGYjJzRDRwaEdQRFlKSloyOEE4VVRiYUZFY255SXRRSWRUUHZxWCtValdrTmZMUjRoU0tCSVBtK3ZRRm93WTVPTHpWRFNCdXhuY0RwR2JoSitpcUdINy9nSlJPOHFSTm5BbUl3aTlIQ0MxRkNWSkJ5Q1YzckxsVjcvZ25NYUhiSlYrYjlXNzB6S01JdGhDWXRicC9Pd1JxekJIRnlvNzBUTmpoYWN3OXpBOGRjQS9GTVM2TVJOOWRuQWdNQkFBR2pnZ0x2TUlJQzZ6QU9CZ05WSFE4QkFmOEVCQU1DQmFBd0hRWURWUjBsQkJZd0ZBWUlLd1lCQlFVSEF3UUdDQ3NHQVFVRkJ3TUNNSUdzQmdOVkhTQUVnYVF3Z2FFd2daUUdDU3Q4aUZBRklBOEVBakNCaGpCQkJnZ3JCZ0VGQlFjQ0FSWTFhSFIwY0RvdkwyUmxiVzh0Y0d0cExtWnBibUV1YUhJdlkzQnpMMk53YzI1eFkyUmxiVzh5TURFMGRqSXRNQzFvY2k1d1pHWXdRUVlJS3dZQkJRVUhBZ0VXTldoMGRIQTZMeTlrWlcxdkxYQnJhUzVtYVc1aExtaHlMMk53Y3k5amNITnVjV05rWlcxdk1qQXhOSFl5TFRBdFpXNHVjR1JtTUFnR0JnUUFqM29CQWpCOUJnZ3JCZ0VGQlFjQkFRUnhNRzh3S0FZSUt3WUJCUVVITUFHR0hHaDBkSEE2THk5a1pXMXZNakF4TkMxdlkzTndMbVpwYm1FdWFISXdRd1lJS3dZQkJRVUhNQUtHTjJoMGRIQTZMeTlrWlcxdkxYQnJhUzVtYVc1aExtaHlMMk5sY25ScFptbHJZWFJwTDJSbGJXOHlNREUwWDNOMVlsOWpZUzVqWlhJd0pRWURWUjBSQkI0d0hJRWFlbVZzYW10dkxuWnlZVzVyYjNabFkydHBRR1pwYm1FdWFISXdnZ0VZQmdOVkhSOEVnZ0VQTUlJQkN6Q0JwcUNCbzZDQm9JWW9hSFIwY0RvdkwyUmxiVzh0Y0d0cExtWnBibUV1YUhJdlkzSnNMMlJsYlc4eU1ERTBMbU55YklaMGJHUmhjRG92TDJSbGJXOHRiR1JoY0M1bWFXNWhMbWh5TDJOdVBVWnBibUVsTWpCRVpXMXZKVEl3UTBFbE1qQXlNREUwTEc4OVJtbHVZVzVqYVdwemEyRWxNakJoWjJWdVkybHFZU3hqUFVoU1AyTmxjblJwWm1sallYUmxVbVYyYjJOaGRHbHZia3hwYzNRbE0wSmlhVzVoY25rd1lLQmVvRnlrV2pCWU1Rc3dDUVlEVlFRR0V3SklVakVkTUJzR0ExVUVDaE1VUm1sdVlXNWphV3B6YTJFZ1lXZGxibU5wYW1FeEdqQVlCZ05WQkFNVEVVWnBibUVnUkdWdGJ5QkRRU0F5TURFME1RNHdEQVlEVlFRREV3VkRVa3d4TnpBZkJnTlZIU01FR0RBV2dCUTdoRm9VOWNVODRVZzdYZEVuTlh2Vlpid09LakFkQmdOVkhRNEVGZ1FVV0tGUzBXWlRiTXJJZ3VQbFNFdkxjUEd5S05rd0NRWURWUjBUQkFJd0FEQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FnRUFnVlNoS3Q1SHhnWnMvZVc4Wk0wTlVXNlZjYWM3a1RQQzRCK0lqaXFBNVl5cGI1clR5dFhUYmV3aU9McHJJbTZ5aWhTT0UvcDB2UWYzbGR3MEtZdXdDOEF4ckxWbmFic2VXMXoveE5wNE9ERUE0VEplMjFidjlPWnV0TWdpV2l1RVV0QnVoTEZadmMrUW9JR3VWdVNPWE1YREJ2bzFrVzZVZCt3WTFXK0ExckpwVlJZNnFjSWU0cUl6aWk5Zno1MkswK0VUczlrNnk2clZleVVpdTdRTEZybnFqbE1ZeXFBYkN5THAvY0hCbW1pTUJzUXhncVpYSE13OXEzYnZuYzgwa3NZMHAzWE5WYzFtVnZsZFFRcEFITUtJbnBPTjJZdHZlMGs2OUpxR2p3T1B5QlplQzFTMFU1UEhuTlF5QXppdEhpeXgzUTBGcFZpRFg1Mm01ZlRUc2wzaW5CZnpaeDBQWFVDeGRxN2dXNTB4UGpYaXlQTHFPS1NSdXNPNkpFWmNBazgvNUVhNVlvdGRzU3J4Wnl1TlMwS2huTEVVOWQ0RnRtb2tWMHo1TUh5N0ZKR1JyQ3ZMMkh3WlpXSWIrUjhHc2Q1Uko5UGtUdllsbUNTeXZhM1p6aE14V0pWbjdnOWs1QVJDcmZITmRhWjdOcVgvL3RXbEFyQjJ4WEFFOFFXUUx2VDBVRmNPNlVXQVkzMzlzVkZWL0FJTnhteENSQ2pGVkdYWUlLR1VYT2g3SHdqZGt0S3ZhK0RsUUEwOHFPeGZOZ1cycFJMZWR6NndHNlBBemhzb2JrK1B4SVNlTmJEckZGZ0JtWGJXbnRtZnovb09XOFlTa3U3ZEkwSi84RDhQZURFa01CelNjc1p2QXY2RUFXdENiQUtTYjFHbVJNZVhiNUY2YjBGb2h3OD08L1g1MDlDZXJ0aWZpY2F0ZT48L1g1MDlEYXRhPjwvS2V5SW5mbz48L1NpZ25hdHVyZT48L1NpZ25hdHVyZXM+PC9TZXJ2aWNlUmVxdWVzdD4='/>
            <input type='hidden' name='ResponseUrl'
                   value='https://eovlastenjatst.fina.hr/Authorizations/ReceiveAuthorizeResponse/3'/>
            <input type='hidden' name='CancelUrl' value='http://localhost:8080/'/>
        </form>
    );
}

export default SimulateAuthorizationServicePage;