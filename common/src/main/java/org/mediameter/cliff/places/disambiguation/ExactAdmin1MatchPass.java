package org.mediameter.cliff.places.disambiguation;

import java.util.ArrayList;
import java.util.List;

import com.bericotech.clavin.gazetteer.FeatureCode;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class ExactAdmin1MatchPass extends GenericPass {
    
    @Override
    protected List<List<ResolvedLocation>> disambiguate(
            List<List<ResolvedLocation>> possibilitiesToDo,
            List<ResolvedLocation> bestCandidates) {
        List<List<ResolvedLocation>> possibilitiesToRemove = new ArrayList<List<ResolvedLocation>>();
        for( List<ResolvedLocation> candidates: possibilitiesToDo){
            if(containsPopulatedCityExactMatch(candidates)){
                continue;
            }
            ResolvedLocation firstCandidate = candidates.get(0);
            if( (isExactMatch(firstCandidate) || isExactMatchToAdmin1(firstCandidate)) &&   /* pick exact matches to name or ADMIN1, to catch state abbreviations */
                    firstCandidate.getGeoname().getPopulation()>0 && 
                    firstCandidate.getGeoname().getFeatureCode().equals(FeatureCode.ADM1)){
                bestCandidates.add(firstCandidate);
                possibilitiesToRemove.add(candidates);
            }
        }
        return possibilitiesToRemove;
    }

    /**
     * Tuned to skip tiny cities that are populated to solve the Oklahoma problem
     * and the Sao Paulo problem.  The population threshold is a subjective number based 
     * on a number of specific test cases we have in the unit tests (from bug reports).
     * @param candidates
     * @return
     */
    private boolean containsPopulatedCityExactMatch(List<ResolvedLocation> candidates) {
        for(ResolvedLocation loc:candidates){
            if(loc.getGeoname().getPopulation()>300000 && isCity(loc) && isExactMatch(loc)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Pick states (Admin1) that are an exact match to help colocation step";
    }
    
}
